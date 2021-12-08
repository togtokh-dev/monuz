package com.togtokh.monuz.adepter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.togtokh.monuz.AppConfig;
import com.togtokh.monuz.R;
import com.togtokh.monuz.list.DownloadLinkList;
import com.togtokh.monuz.list.MultiqualityList;
import com.togtokh.monuz.list.YTStreamList;
import com.togtokh.monuz.utils.DownloadHelper;
import com.togtokh.monuz.utils.HelperUtils;
import com.togtokh.monuz.utils.Utils;
import com.togtokh.monuz.utils.Yts;
import com.togtokh.monuz.utils.stream.Vimeo;
import com.togtokh.monuz.utils.stream.Yandex;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadLinkListAdepter extends RecyclerView.Adapter<DownloadLinkListAdepter.MyViewHolder> {
    private Context mContext;
    private View rootView;
    private Dialog downloadDialog;
    private List<DownloadLinkList> mData;
    LowCostVideo xGetter;
    static ProgressDialog progressDialog;
    String title = "";

    public DownloadLinkListAdepter(Context mContext, View mView, Dialog mDialog, List<DownloadLinkList> mData) {
        this.mContext = mContext;
        this.rootView = mView;
        this.downloadDialog = mDialog;
        this.mData = mData;
    }

    @NonNull
    @NotNull
    @Override
    public DownloadLinkListAdepter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.download_link_item,parent,false);
        return new DownloadLinkListAdepter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DownloadLinkListAdepter.MyViewHolder holder, int position) {
        holder.setName(mData.get(position));
        holder.setQuality(mData.get(position));
        holder.setSize(mData.get(position));

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCancelable(false);

        xGetter = new LowCostVideo(mContext);
        xGetter.onFinish(new LowCostVideo.OnTaskCompleted() {

            @Override
            public void onTaskCompleted(ArrayList<XModel> vidURL, boolean multiple_quality) {
                progressDialog.dismiss();
                if (multiple_quality){
                    if (vidURL!=null) {
                        //This video you can choose qualities
                        for (XModel model : vidURL) {
                            //If google drive video you need to set cookie for play or download
                        }
                        multipleQualityDialog(vidURL);
                    }else done(null);
                }else {
                    done(vidURL.get(0));
                }
            }

            @Override
            public void onError() {
                progressDialog.dismiss();
                done(null);
            }
        });

        holder.link_card.setOnClickListener(view -> {
            if(HelperUtils.checkStoragePermission(mContext)) {
                downloadDialog.dismiss();
                if (mData.get(position).getDownload_type().equals("External")) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mData.get(position).getUrl())));
                } else if (mData.get(position).getDownload_type().equals("Internal")) {
                    if (mData.get(position).getType().equals("Mp4")) {
                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", mData.get(position).getUrl());
                    } else if (mData.get(position).getType().equals("Mkv")) {
                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mkv", mData.get(position).getUrl());
                    } else if (mData.get(position).getType().equals("Facebook")) {
                        title = mData.get(position).getName();
                        Uri uri = Uri.parse(mData.get(position).getUrl());
                        String v = uri.getQueryParameter("v");
                        progressDialog.show();
                        xGetter.find(v);
                    } else if (mData.get(position).getType().equals("GoogleDrive")) {
                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", AppConfig.url + "/api/fetch/gdfetch.php?url=" + mData.get(position).getUrl());
                    /*String[] parts = mData.get(position).getUrl().split("/");
                    String id = parts[5];
                    String finalurl = "https://drive.google.com/uc?export=download&id="+id;
                    DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", finalurl);*/
                    } else if (mData.get(position).getType().equals("Onedrive")) {
                        String encodedUrl = Utils.toBase64(mData.get(position).getUrl());
                        String finalEncodedUrl = "https://api.onedrive.com/v1.0/shares/u!" + encodedUrl + "/root/content";
                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", finalEncodedUrl);
                    } else if (mData.get(position).getType().equals("Yandex")) {
                        progressDialog.show();
                        Yandex.getStreamLink(mContext, mData.get(position).getUrl(), new Yandex.yandexCallback() {

                            @Override
                            public void onSuccess(String result) {
                                progressDialog.hide();
                                DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", result);
                            }

                            @Override
                            public void onError(VolleyError error) {
                                progressDialog.hide();
                            }
                        });
                    } else if (mData.get(position).getType().equals("Vimeo")) {
                        progressDialog.show();
                        Vimeo.getStreamLink(mContext, mData.get(position).getUrl(), new Vimeo.vimeoCallback() {

                            @Override
                            public void onSuccess(JsonArray result) {
                                List<MultiqualityList> multiqualityList = new ArrayList<>();
                                for (JsonElement rootElement : result) {
                                    JsonObject rootObject = rootElement.getAsJsonObject();

                                    String quality = rootObject.get("quality").getAsString();
                                    String url = rootObject.get("url").getAsString();

                                    multiqualityList.add(new MultiqualityList(quality, url));
                                }

                                CharSequence[] name = new CharSequence[multiqualityList.size()];

                                for (int i = 0; i < multiqualityList.size(); i++) {
                                    name[i] = multiqualityList.get(i).getQuality();
                                }

                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                                        .setTitle("Quality!")
                                        .setItems(name, (dialog, which) -> {
                                            DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", multiqualityList.get(which).getUrl());
                                        })
                                        .setPositiveButton("Close", (dialog, which) -> {
                                            dialog.dismiss();
                                        });
                                progressDialog.hide();
                                builder.show();
                            }

                            @Override
                            public void onError(VolleyError error) {
                                progressDialog.hide();
                            }
                        });
                    } else if (mData.get(position).getType().equals("StreamTape")) {
                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", AppConfig.url + "/api/fetch/steamtape/stfetch.php?url=" + mData.get(position).getUrl());

                    } else if (mData.get(position).getType().equals("Youtube")) {

                        progressDialog.show();
                        Yts.getlinks(mContext, mData.get(position).getUrl(), new Yts.VolleyCallback() {
                            @Override
                            public void onSuccess(List<YTStreamList> result) {
                                progressDialog.dismiss();
                                Collections.reverse(result);
                                CharSequence[] name = new CharSequence[result.size()];
                                CharSequence[] vid = new CharSequence[result.size()];
                                CharSequence[] token = new CharSequence[result.size()];
                                for (int i = 0; i < result.size(); i++) {
                                    name[i] = result.get(i).getName();
                                    vid[i] = result.get(i).getVid();
                                    token[i] = result.get(i).getToken();
                                }


                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                                        .setTitle("Quality!")
                                        .setItems(name, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Yts.getStreamLinks(mContext, (String) token[which], (String) vid[which], new Yts.VolleyCallback2() {

                                                    @Override
                                                    public void onSuccess(String result) {
                                                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", result);
                                                    }

                                                    @Override
                                                    public void onError(VolleyError error) {
                                                    }
                                                });
                                            }
                                        })
                                        .setPositiveButton("Close", (dialog, which) -> {
                                            dialog.dismiss();
                                        });
                                builder.show();
                            }

                            @Override
                            public void onError(VolleyError error) {
                            }
                        });

                    } else if (mData.get(position).getType().equals("Dropbox")) {
                        /*progressDialog.show();

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> {
                            VideoInfo streamInfo = null;
                            try {
                                YoutubeDL.getInstance().init(mContext);
                                try {
                                    streamInfo = YoutubeDL.getInstance().getInfo(mData.get(position).getUrl());
                                } catch (YoutubeDLException | InterruptedException e) {
                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                }
                            } catch (YoutubeDLException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                            }

                            progressDialog.dismiss();
                            DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", streamInfo.getUrl());

                        }, 10);*/

                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", mData.get(position).getUrl()+"?dl=1");


                    } else if (mData.get(position).getType().equals("MP4Upload") || mData.get(position).getType().equals("GooglePhotos") || mData.get(position).getType().equals("MediaFire") || mData.get(position).getType().equals("OKru") ||
                            mData.get(position).getType().equals("VK") || mData.get(position).getType().equals("Twitter") || mData.get(position).getType().equals("Solidfiles") || mData.get(position).getType().equals("Vidoza") ||
                            mData.get(position).getType().equals("UpToStream") || mData.get(position).getType().equals("Fansubs") || mData.get(position).getType().equals("Sendvid") || mData.get(position).getType().equals("Fembed") || mData.get(position).getType().equals("Filerio") ||
                            mData.get(position).getType().equals("Megaup") || mData.get(position).getType().equals("GoUnlimited") || mData.get(position).getType().equals("Cocoscope") || mData.get(position).getType().equals("Vidbm") || mData.get(position).getType().equals("Pstream") ||
                            mData.get(position).getType().equals("vlare") || mData.get(position).getType().equals("StreamWiki") || mData.get(position).getType().equals("Vivosx") || mData.get(position).getType().equals("BitTube") || mData.get(position).getType().equals("VideoBin") ||
                            mData.get(position).getType().equals("4shared") || mData.get(position).getType().equals("vudeo")) {
                        title = mData.get(position).getName();
                        progressDialog.show();
                        xGetter.find(mData.get(position).getUrl());
                    } else {
                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", mData.get(position).getUrl());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView link_card;
        TextView linkName;
        TextView linkQuality;
        TextView linkSize;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            link_card = itemView.findViewById(R.id.link_card);
            linkName = itemView.findViewById(R.id.linkName);
            linkQuality = itemView.findViewById(R.id.linkQuality);
            linkSize = itemView.findViewById(R.id.linkSize);
        }

        void setName(DownloadLinkList nameText) {
            linkName.setText(nameText.getName());
        }
        void setQuality(DownloadLinkList qualityText) {
            linkQuality.setText(qualityText.getQuality());
        }
        void setSize(DownloadLinkList sizeText) {
            linkSize.setText(", "+sizeText.getSize());
        }
    }

    private void multipleQualityDialog(ArrayList<XModel> model) {
        CharSequence[] name = new CharSequence[model.size()];

        for (int i = 0; i < model.size(); i++) {
            name[i] = model.get(i).getQuality();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle("Quality!")
                .setItems(name, (dialog, which) -> done(model.get(which)))
                .setPositiveButton("Close", (dialog, which) -> {
                    dialog.dismiss();
                });
        builder.show();
    }

    private void done(XModel xModel){
        String url = null;
        if (xModel!=null) {
            url = xModel.getUrl();
        }
        if (url!=null) {
            DownloadHelper.startDownload(mContext, rootView, title, "mp4", xModel.getUrl());
        }else {
            Log.d("test", "inValid URL");
        }
    }
}
