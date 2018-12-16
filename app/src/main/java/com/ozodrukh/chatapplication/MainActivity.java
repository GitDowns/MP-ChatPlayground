package com.ozodrukh.chatapplication;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, View.OnLongClickListener {

  private final MessagesAdapter adapter = new MessagesAdapter();
  private RecyclerView contentView;
  private EditText messageInputView;

  private Dialog pendingDisplayedDialog;

  private final TextView.OnEditorActionListener onEditingActions =
      new TextView.OnEditorActionListener() {
        @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
          if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER
              && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
          }
          return false;
        }
      };

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Picasso.setSingletonInstance(new Picasso.Builder(this)
        .loggingEnabled(true)
        .build());

    setContentView(R.layout.activity_main);

    messageInputView = findViewById(R.id.input_message);
    messageInputView.setOnEditorActionListener(onEditingActions);

    final LinearLayoutManager lm = new LinearLayoutManager(this);
    lm.setReverseLayout(false);
    lm.setStackFromEnd(true);

    contentView = findViewById(R.id.messages_container);
    contentView.setLayoutManager(lm);
    contentView.setAdapter(adapter);

    findViewById(R.id.action_send_message)
        .setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            onMessageSend(null);
          }
        });

    findViewById(R.id.action_select_image).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        final View container = LayoutInflater.from(v.getContext())
            .inflate(R.layout.dialog_select_image, null, false);

        final EditText imageInputView = container.findViewById(R.id.input_image);

        container.findViewById(R.id.action_send_message)
            .setOnClickListener(new View.OnClickListener() {
              @Override public void onClick(View v) {
                if (pendingDisplayedDialog != null) {
                  pendingDisplayedDialog.dismiss();
                }

                onMessageSend(imageInputView.getText().toString());
              }
            });

        pendingDisplayedDialog =
            new AlertDialog.Builder(v.getContext(), R.style.Theme_AppCompat_Dialog)
                .setCancelable(true)
                .setView(container)
                .setTitle("Attach image")
                .create();

        pendingDisplayedDialog.show();
      }
    });

    adapter.addMessage(
        new Message(0,
            "https://poster.nicefon.ru/2016_12/20/800x450/189696e2bc985098b8b0f0.jpg",
            "hi"));
    adapter.addMessage(new Message(1, null, "hello"));
    adapter.addMessage(
        new Message(0,
            "https://poster.nicefon.ru/2016_12/20/800x450/189696e2bc985098b8b0f0.jpg",
            ""));

    adapter.setOnMessageClickListener(this);
    adapter.setOnLongClickListener(this);
  }

  private void onMessageSend(String imageUrl) {
    final String text = messageInputView.getText().toString();

    adapter.addMessage(new Message(adapter.getItemCount() % 2, imageUrl, text));
    messageInputView.setText(null);
    contentView.scrollToPosition(adapter.getItemCount() - 1);
  }

  @Override public void onClick(View v) {

  }

  @Override public boolean onLongClick(View v) {
    final int adapterPosition = contentView.getChildAdapterPosition(v);
    final Message message = adapter.getMessage(adapterPosition);

    new AlertDialog.Builder(this)
        .setTitle("Message Actions")
        .setItems(new CharSequence[] { "Copy" }, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            switch (which) {
              case 0:
                final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                if (cm != null) {
                  cm.setPrimaryClip(ClipData.newPlainText("message", message.getText()));
                }
                break;
            }
          }
        })
        .show();
    return true;
  }

  class Message {
    private final long uid;
    private final String imageUrl;
    private final CharSequence text;

    Message(long uid, @Nullable String imageUrl, CharSequence text) {
      this.uid = uid;
      this.imageUrl = imageUrl;
      this.text = text;
    }

    public boolean hasImage() {
      return !TextUtils.isEmpty(getImageUrl());
    }

    public String getImageUrl() {
      return imageUrl;
    }

    public boolean isMyMessage() {
      return uid == 0;
    }

    public CharSequence getText() {
      return text;
    }
  }

  static class MessagesAdapter extends RecyclerView.Adapter<TextMessageCell> {
    private final List<Message> messages = new ArrayList<>();
    private View.OnClickListener onMessageClickListener;
    private View.OnLongClickListener onLongClickListener;

    public void addMessage(Message message) {
      messages.add(message);
      notifyItemInserted(messages.size() - 1);
    }

    public Message getMessage(int position) {
      return messages.get(position);
    }

    public void setOnMessageClickListener(View.OnClickListener onMessageClickListener) {
      this.onMessageClickListener = onMessageClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
      this.onLongClickListener = onLongClickListener;
    }

    @NonNull @Override
    public TextMessageCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new TextMessageCell(LayoutInflater.from(parent.getContext())
          .inflate(R.layout.message_cell, parent, false));
    }

    @Override public void onViewAttachedToWindow(@NonNull TextMessageCell holder) {
      super.onViewAttachedToWindow(holder);
      holder.itemView.setOnClickListener(onMessageClickListener);
      holder.itemView.setOnLongClickListener(onLongClickListener);
    }

    @Override public void onViewDetachedFromWindow(@NonNull TextMessageCell holder) {
      super.onViewDetachedFromWindow(holder);
      holder.itemView.setOnClickListener(null);
      holder.itemView.setOnLongClickListener(null);
    }

    @Override public void onBindViewHolder(@NonNull TextMessageCell cell, int position) {
      final Message message = getMessage(position);
      cell.message.setText(message.getText());

      final FrameLayout.LayoutParams lp =
          (FrameLayout.LayoutParams) cell.container.getLayoutParams();

      if (message.isMyMessage()) {
        lp.gravity = Gravity.LEFT;
      } else {
        lp.gravity = Gravity.RIGHT;
      }

      if (TextUtils.isEmpty(message.getText())) {
        cell.message.setVisibility(View.GONE);
      } else {
        cell.message.setVisibility(View.VISIBLE);
      }

      if (!message.hasImage()) {
        cell.image.setImageDrawable(null);
        cell.image.setVisibility(View.GONE);
      } else {
        cell.image.setVisibility(View.VISIBLE);
        final int maxWidth = (int) (cell.itemView.getResources().getDisplayMetrics().density * 220);

        Picasso.get()
            .load(message.getImageUrl())
            .resize(maxWidth, 0)
            .placeholder(new ColorDrawable(Color.LTGRAY))
            .centerCrop()
            .config(Bitmap.Config.RGB_565)
            .into(cell.image);
      }
    }

    @Override public int getItemCount() {
      return messages.size();
    }
  }

  static class TextMessageCell extends RecyclerView.ViewHolder {

    private final ImageView image;
    private final TextView message;
    private final ViewGroup container;

    public TextMessageCell(@NonNull View itemView) {
      super(itemView);
      container = itemView.findViewById(R.id.message_cell);
      message = itemView.findViewById(R.id.message_text);
      image = itemView.findViewById(R.id.message_image);
    }
  }
}
