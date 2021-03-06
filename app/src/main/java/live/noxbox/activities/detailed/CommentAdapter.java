package live.noxbox.activities.detailed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import live.noxbox.R;
import live.noxbox.model.Comment;
import live.noxbox.tools.DateTimeFormatter;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;

    public CommentAdapter() { }

    public CommentAdapter(Collection<Comment> comments) {
        this.comments = Lists.newArrayList(comments);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        if(comments.get(position).getLike()){
            holder.like.setImageResource(R.drawable.like);
        }else{
            holder.like.setImageResource(R.drawable.dislike);
        }
        holder.text.setText(comments.get(position).getText());
        holder.time.setText(DateTimeFormatter.date(comments.get(position).getTime()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView like;
        TextView text;
        TextView time;

        CommentViewHolder(View layout) {
            super(layout);
            like = layout.findViewById(R.id.like);
            text = layout.findViewById(R.id.text);
            time = layout.findViewById(R.id.time);
        }
    }
}
