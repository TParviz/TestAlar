package ru.main.testalar;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class LoadingViewHolder extends RecyclerView.ViewHolder {

    ProgressBar progressBar;

    LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progress_bar);
    }
}

class ItemViewHolder extends RecyclerView.ViewHolder{

    TextView tvTitle;
    ImageView imageView;

    ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.card_view_text);
        imageView = itemView.findViewById(R.id.card_view_image);
    }
}

public class AdapterClass extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;

    private List<Model> dataModel;
    private LMInterface loadMore;
    private boolean isLoading;
    private Activity mActivity;
    private int visibleThreshold = 5;
    private int lastVisibleItemPosition, totalItemsCount;
    private RecyclerView recyclerView;

    public static final String STRING_ID = "STRING_ID";
    public static final String STRING_NAME = "STRING_NAME";
    public static final String STRING_COUNTRY = "STRING_COUNTRY";
    public static final String STRING_LAT = "STRING_LAT";
    public static final String STRING_LON = "STRING_LON";
    private final String IMAGE_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_WksSdmkC4ypY4OrzXqE3g-bYc7paHSd51w&usqp=CAU";

    AdapterClass(Activity mActivity, RecyclerView recyclerView, List<Model> dataModel) {
        this.mActivity = mActivity;
        this.dataModel = dataModel;
        this.recyclerView = recyclerView;

        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                assert manager != null;
                totalItemsCount = manager.getItemCount();
                lastVisibleItemPosition = manager.findLastVisibleItemPosition();
                if (!isLoading && totalItemsCount < (lastVisibleItemPosition + visibleThreshold)){
                    if (loadMore != null)
                        loadMore.onLoadMore();
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return dataModel.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    void setLoadMore(LMInterface loadMore) {
        this.loadMore = loadMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(mActivity).inflate(R.layout.element_view, viewGroup, false);
            view.setOnClickListener(v -> {
                Model mapPointsClass = dataModel.get(recyclerView.getChildLayoutPosition(v));
                Intent intent = new Intent(mActivity, MapCallback.class);
                intent.putExtra(STRING_ID, mapPointsClass.getId());
                intent.putExtra(STRING_NAME, mapPointsClass.getName());
                intent.putExtra(STRING_COUNTRY, mapPointsClass.getCountry());
                intent.putExtra(STRING_LAT, mapPointsClass.getLat());
                intent.putExtra(STRING_LON, mapPointsClass.getLon());
                view.getContext().startActivity(intent);
            });
            return new ItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.loading_view, viewGroup, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder){
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.tvTitle.setText(dataModel.get(position).getName());
            viewHolder.imageView.setTag(IMAGE_URL);
            new ImageLoader().execute(viewHolder.imageView);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    void setLoaded(){
        isLoading = false;
    }
}