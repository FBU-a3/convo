package a3.com.convo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import a3.com.convo.R;

public class AdditionalLikeAdapter extends RecyclerView.Adapter<AdditionalLikeAdapter.ViewHolder> {
    private List <String> mLikes;
    Context context;
    public AdditionalLikeAdapter(List<String> likes) {
        mLikes = likes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View likeView = inflater.inflate(R.layout.item_like, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(likeView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String likeString = (String) mLikes.get(i);
        viewHolder.tvLike.setText(likeString);
    }

    public int getItemCount() {
        return mLikes.size();
    }

    public void clear() {
        mLikes.clear();
        notifyDataSetChanged();
    }

    public void add(String like) {
        mLikes.add(like);
        notifyItemInserted(0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvLike;
        public ViewHolder(View itemView) {
            super(itemView);
            tvLike = (TextView) itemView.findViewById(R.id.tv_liked_item);

        }

        @Override
        public void onClick(View view) {

        }
    }
}
