package com.google.vrtoolkit.cardboard.samples.treasurehunt;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Quentin on 10/05/2016.
 */
public class RecyclerViewFolderPDF extends RecyclerView.Adapter<RecyclerViewFolderPDF.ViewHolder> {

    private Context context;
    private ArrayList<FolderOrPDF> folderOrPDF = new ArrayList<FolderOrPDF>();

    RecyclerViewFolderPDF(ArrayList<FolderOrPDF> folderOrPDF, Context context){
        setFolderOrPDF(folderOrPDF);
        this.context = context;
    }

    //Rafraichissement de la liste de documents depuis le fragment
    public void setFolderOrPDF(ArrayList<FolderOrPDF> folderOrPDF){
        this.folderOrPDF = folderOrPDF;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_item_list, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final FolderOrPDF document = this.folderOrPDF.get(position);

        //Affichage du nom du document
        String name = document.getName();
        viewHolder.textView.setText(name);

        //setOnClickListener
        if(document.getType().equals("pdf")){
            viewHolder.imageView.setImageResource(R.drawable.ic_file_document_white);
            viewHolder.linearItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, TreasureHuntActivityBIS.class);
                    intent.putExtra("PDFFile", document.getName());
                    intent.putExtra("path", document.getPath());
                    context.startActivity(intent);
                }
            });
        }
        else {
            viewHolder.imageView.setImageResource(R.drawable.ic_folder_white);
            viewHolder.linearItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListFilesActivity.class);
                    intent.putExtra("path", document.getPath());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.folderOrPDF.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public LinearLayout linearItem;
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView){
            super(itemView);
            linearItem = (LinearLayout) itemView.findViewById(R.id.layout_item);
            imageView = (ImageView) itemView.findViewById(R.id.img_item);
            textView = (TextView) itemView.findViewById(R.id.path_item);
        }
    }

}
