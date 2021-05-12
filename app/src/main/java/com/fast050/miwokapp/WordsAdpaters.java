package com.fast050.miwokapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

class WordsAdpaters extends RecyclerView.Adapter<WordsAdpaters.ViewHolderWords>
{


    private List<Words> provideList;
    //color for background of the RecyclerView list
    private int colorTheme;
    private OnClickItemRecyclerView mOnClickItemRecyclerView;



    interface OnClickItemRecyclerView
    {
        void OnClickItem(int position,List<Words> list);
    }


    public WordsAdpaters(OnClickItemRecyclerView onclick,List<Words> provideList,int colorTheme) {
        this.provideList = provideList;
        this.colorTheme=colorTheme;
        this.mOnClickItemRecyclerView=onclick;

    }

    @NonNull
    @Override
    public ViewHolderWords onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.items_recycler_view_layout,parent,false
               );
        return new ViewHolderWords(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderWords holder, int position) {

        holder.constraintLayout.setBackgroundResource(this.colorTheme);

        Words words= provideList.get(position);
        holder.MiwokWordText.setText(words.getMiwokWord());
        holder.DefaultTranslationWordText.setText(words.getDefaultTranslationWork());
        if(words.getImageResource()!=null)
        Picasso.get().load(words.getImageResource()).into(holder.image);
        else
            holder.image.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return provideList.size();
    }

    class ViewHolderWords extends RecyclerView.ViewHolder
    {
        private TextView MiwokWordText;
        private TextView DefaultTranslationWordText;
        private ImageView image;
        private ConstraintLayout constraintLayout;


        public ViewHolderWords(@NonNull View itemView) {
            super(itemView);
            MiwokWordText=itemView.findViewById(R.id.Miwok_item_text);
            DefaultTranslationWordText=itemView.findViewById(R.id.defaultTranslation_item_text);
            image=itemView.findViewById(R.id.image_item_text);
            constraintLayout=itemView.findViewById(R.id.Constraintlayout_item_list);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickItemRecyclerView.OnClickItem(getAdapterPosition(),provideList);
                }
            });

        }
    }

}
