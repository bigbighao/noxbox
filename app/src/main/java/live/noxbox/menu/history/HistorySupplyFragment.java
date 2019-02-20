package live.noxbox.menu.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import live.noxbox.R;
import live.noxbox.model.MarketRole;

public class HistorySupplyFragment extends Fragment {
    private LinearLayoutManager linearLayoutManager;

    public HistorySupplyFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        RecyclerView listItems = view.findViewById(R.id.recyclerView);
        listItems.setLayoutManager(linearLayoutManager);
        HistoryAdapter adapter = new HistoryAdapter((HistoryActivity) getActivity(), listItems, MarketRole.supply);
        container.findViewById(R.id.chooseService);
        listItems.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }
}

