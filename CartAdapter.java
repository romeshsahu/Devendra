package com.ealpha.cart;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ealpha.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ps.DTO.CartsDTO;

public class CartAdapter extends BaseAdapter {
    ArrayList<CartsDTO> cartsDTOs;
    private Context context;
    LayoutInflater inflater;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public CartAdapter(Context context, ArrayList<CartsDTO> cartsDTOs) {
        this.cartsDTOs = cartsDTOs;
        this.context = context;
        inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return cartsDTOs.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // convert view = design
        // View v = convertView;
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cart_list_row, null);
            holder = new ViewHolder();
            holder.product_image = (ImageView) convertView
                    .findViewById(R.id.product_image_c);
            holder.product_name = (TextView) convertView
                    .findViewById(R.id.product_headline_c);
            holder.product_rs = (TextView) convertView
                    .findViewById(R.id.tv_rs_cart);
            holder.product_quantity = (TextView) convertView
                    .findViewById(R.id.qty_cart);
            holder.product_colour = (TextView) convertView
                    .findViewById(R.id.color_in_cart);
            holder.product_size = (TextView) convertView
                    .findViewById(R.id.size_in_cart);
            holder.qty_minus = (ImageView) convertView
                    .findViewById(R.id.qty_minus);
            holder.qty_plus = (ImageView) convertView
                    .findViewById(R.id.qty_plus);
            holder.remove_view = (LinearLayout) convertView
                    .findViewById(R.id.remove_view_cart);
            holder.total_cart = (TextView) convertView
                    .findViewById(R.id.total_cart);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            holder.product_name.setText(""
                    + cartsDTOs.get(position).getProduct_name());
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            ImageLoader.getInstance().displayImage(
                    cartsDTOs.get(position).getProduct_img(), holder.product_image,
                    options);
        } catch (Exception e) {

        }
        try {
            holder.product_rs.setText(" " + cartsDTOs.get(position).getTotal_price());
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            holder.product_quantity.setText("quantity-"
                    + cartsDTOs.get(position).getQuantity());
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            if (cartsDTOs.get(position).getColor_name().length() > 0) {
                holder.product_colour.setText("colour-"
                        + cartsDTOs.get(position).getColor_name());
            } else {
                holder.product_colour.setText("");
            }
        } catch (Exception e) {
            // TODO: handle exception
            holder.product_colour.setText("");
        }

        try {
            if (cartsDTOs.get(position).getSize().length() > 0) {
                holder.product_size.setText("size-"
                        + cartsDTOs.get(position).getSize());
            } else {
                holder.product_size.setText("");
            }
        } catch (Exception e) {
            // TODO: handle exception
            holder.product_size.setText("");
        }

        holder.remove_view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    CartFragment.myCartFragment.removeAction(position);
                } catch (ClassCastException e) {
                    // TODO: handle exception
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });

        holder.qty_minus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    CartFragment.myCartFragment.cartQtyDescrenment(position);
                } catch (ClassCastException e) {
                    // TODO: handle exception
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });

        holder.qty_plus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    CartFragment.myCartFragment.cartQtyIncrenment(position);
                } catch (ClassCastException e) {
                    // TODO: handle exception
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
        holder.product_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CartFragment.myCartFragment.detailFromCart(position);
                } catch (ClassCastException e) {
                    // TODO: handle exception
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        public ImageView product_image, qty_minus, qty_plus;
        TextView total_cart;
        public TextView id_product, product_name, product_rs, availability,
                product_quantity, product_size, product_colour;
        private LinearLayout remove_view;
    }

}
