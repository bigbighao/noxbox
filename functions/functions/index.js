const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const noxbox = require('./noxbox-functions');
const wallet = require('./wallet-functions');
const http = require('request');

exports.createWalletForNewUser = functions.database.ref('/profiles/{userId}').onCreate((snapshot, context) => {
    return wallet.create({'id' : context.params.userId}).then(
           noxbox.createRating).then(
           noxbox.updateBalance).then(
           noxbox.notifyBalanceUpdated);
});

exports.balance = functions.database.ref('/requests/{userID}/balance').onCreate(event => {
    return noxbox.getWallet(event.val()).then(
           wallet.getBalance).then(
           noxbox.updateBalance).then(
           noxbox.notifyBalanceUpdated, noxbox.logError).then(
           noxbox.releaseRequest);
});

exports.refund = functions.database.ref('/requests/{userID}/refund').onCreate(event => {
    return noxbox.getWallet(event.val()).then(
           wallet.getBalance).then(
           noxbox.isEnoughMoneyForRefund).then(
           wallet.refund).then(
           noxbox.updateBalance).then(
           noxbox.notifyBalanceUpdated, noxbox.logError).then(
           noxbox.releaseRequest);
});

exports.request = functions.database.ref('/requests/{userId}/request').onCreate(event => {
    return noxbox.getWallet(event.val()).then(
           wallet.getBalance).then(
           noxbox.getPrice).then(
           noxbox.isEnoughMoneyForRequest).then(
           noxbox.getPayerProfile).then(
           noxbox.getPerformerProfile).then(
           noxbox.createNoxbox).then(
           noxbox.pingPerformer).then(
           noxbox.freezeMoney).then(
           noxbox.notifyBalanceUpdated, noxbox.logError).then(
           noxbox.releaseRequest);
});

exports.cancel = functions.database.ref('/requests/{userId}/cancel').onCreate(event => {
    return noxbox.cancelNoxbox(event.val()).then(
           noxbox.notifyCanceled).then(
           // TOdO (nli) pay in case more then 3 minutes gone
           noxbox.unfreezeMoneyInTransaction).then(
           noxbox.notifyPayerBalanceUpdated, noxbox.logError).then(
           noxbox.releaseRequest);
});

exports.accept = functions.database.ref('/requests/{userId}/accept').onCreate(event => {
    return noxbox.acceptNoxbox(event.val()).then(
           noxbox.generateSecret).then(
           noxbox.notifyAccepted, noxbox.logError).then(
           noxbox.releaseRequest);
});

exports.complete = functions.database.ref('/requests/{userId}/complete').onCreate(event => {
    // TODO (nli) check if there is enough time from accepted to completed for performing one action
    return noxbox.completeNoxbox(event.val()).then(
           noxbox.notifyCompleted).then(
           noxbox.getPayerSeed).then(
           noxbox.getPerformerAddress).then(
           // TODO (nli) send fee to the default label in the same time
           wallet.pay).then(
           // TODO (nli) create fee in performers wallet
           noxbox.storePriceWithoutFeeInNoxbox).then(
           noxbox.updatePayerBalanceInTransaction).then(
           noxbox.notifyPayerBalanceUpdated).then(
           noxbox.likePerformer).then(
           noxbox.likePayer, noxbox.logError).then(
           noxbox.releaseRequest);
});

exports.moneyBack = functions.database.ref('/requests/{userId}/moneyBack').onCreate(event => {
           // TODO (nli) update noxbox with moneyBackTime (one issue available)
    return noxbox.moneyBackNoxbox(event.val()).then(
           // TODO (nli) create issue
           // TODO (nli) notify Money Back
           noxbox.notifyMoneyBack, noxbox.logError).then(
           noxbox.releaseRequest);
});

// tODO (nli) request comment issue

// TODO (nli) resolve issue with dislike, like, moneyBack for performer
// TODO (nli) check voting results, money back in case of (votes > 10) and (moneyBack > 60%)
// TODO (nli) in case of (votes > 24) and (no one side > 60%) dislike

exports.dislike = functions.database.ref('/requests/{userId}/dislike').onCreate(event => {
    return noxbox.dislikeNoxbox(event.val()).then(
           noxbox.blacklist).then(
           noxbox.updateRatings).then(
           noxbox.notifyDisliked, noxbox.logError).then(
           noxbox.releaseRequest);
});

exports.about = functions.https.onRequest((request, response) => { return response.send("Dedicated to my dear wife Maria"); });

exports.showFunctionIp = functions.https.onRequest((request, response) => {
    return http("http://www.showmyip.gr/", { 'json': true }, (err, res, body) => { return response.send(body); });
});

exports.pushNotificationTest = functions.https.onRequest((request, response) => {
    if(request.query.key) {
        var message = {
            data: {
                type: 'ping',
                icon: 'https://lh3.googleusercontent.com/-zM3Lclpg7fQ/AAAAAAAAAAI/AAAAAAAAGa0/wDqyqJtdfd0/s96-c/photo.jpg'
            },
            android: {
                ttl: 1000 * 30,
                priority: 'high',
            },
            token: request.query.key
        };

        console.log(message);

        admin.messaging().send(message)
          .then((response) => {
            console.log('Successfully sent message:', response);
          })
          .catch((error) => {
            console.log('Error sending message:', error);
          });
    }

    return response.send("push was sent");
});



