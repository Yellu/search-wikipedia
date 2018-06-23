package com.wiki.searchwikipedia.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.wiki.searchwikipedia.R;
import com.wiki.searchwikipedia.database.SearchPageEntity;
import com.wiki.searchwikipedia.eventbus.WikiPageEvent;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SearchPageEntity> searchPageEntities;
    public SearchResultAdapter(List<SearchPageEntity> searchPageEntities){
        updateAdapter(searchPageEntities);
    }

    public void updateAdapter(List<SearchPageEntity> searchPageEntities){
        this.searchPageEntities = searchPageEntities;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_list_item, viewGroup, false);
        return new WikiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        WikiViewHolder holder = (WikiViewHolder) viewHolder;
        try {
            SearchPageEntity pageEntity = searchPageEntities.get(i);
            if (pageEntity == null || !pageEntity.isValid()){
                return;
            }
            holder.bindTo(pageEntity);
        } catch (IndexOutOfBoundsException e){

        }
    }

    @Override
    public int getItemCount() {
        return searchPageEntities.size();
    }

    static class WikiViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.img_profile)
        ImageView thumb;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_description)
        TextView tvDescription;

        public WikiViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindTo(SearchPageEntity pageEntity){
            String imageUrl = pageEntity.getThumbUrl();
            String description = pageEntity.getDescription();
            String title = pageEntity.getTitle();

            tvTitle.setText(title);
            tvDescription.setText(description);

            Glide.with(itemView.getContext())
                    .asBitmap()
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.gallery_placeholder)
                            .error(R.drawable.gallery_placeholder))
                    .into(thumb);

            itemView.setOnClickListener(v->{
                if (TextUtils.isEmpty(title)){
                    return;
                }
                EventBus.getDefault().post(new WikiPageEvent(title));
            });
        }
    }
}
