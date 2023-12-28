package com.example.ecommerce.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.adapters.MyCartAdapter;
import com.example.ecommerce.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    double overAllTotalAmount;
    TextView overAllAmount;
    Toolbar toolbar;
    RecyclerView recyclerView;
    List<MyCartModel> cartModelList;
    MyCartAdapter cartAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.my_cart_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get data from my cart adapter
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter("MyTotalAmount"));

        overAllAmount = findViewById(R.id.textView3);
        recyclerView = findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(this, cartModelList);
        recyclerView.setAdapter(cartAdapter);

        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                                String documentId = doc.getId();
                                myCartModel.setDocumentId(documentId);

                                cartModelList.add(myCartModel);
                                cartAdapter.notifyDataSetChanged();
                            }
                        }
                        updateTotalAmount();  // Cập nhật tổng tiền khi dữ liệu thay đổi
                    }
                });

        Button buyNowButton = findViewById(R.id.buy_now);
        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức để chuyển sang PaymentActivity khi nhấn nút "Buy Now"
                onBuyNowButtonClick(v);
            }
        });
    }

    // Phương thức xử lý sự kiện khi nhấn nút "Buy Now"
    public void onBuyNowButtonClick(View view) {
        // Tính toán tổng và subtotal
        double subtotal = calculateSubtotal();
        double total = calculateTotal(subtotal);

        // Chuyển sang PaymentActivity và truyền giá trị subtotal và total
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("subtotal", subtotal);
        intent.putExtra("total", total);
        startActivity(intent);
    }

    // Tính tổng tiền khi dữ liệu thay đổi
    private void updateTotalAmount() {
        overAllTotalAmount = calculateSubtotal();
        overAllAmount.setText("Total Amount: " + overAllTotalAmount + "$");
    }

    // Phương thức tính tổng số tiền trong giỏ hàng
    // Phương thức tính tổng số tiền trong giỏ hàng
    private double calculateSubtotal() {
        double subtotal = 0.0;

        // Lặp qua danh sách sản phẩm trong giỏ hàng để tính subtotal
        for (int i = 0; i < cartModelList.size(); i++) {
            MyCartModel cartModel = cartModelList.get(i);

            // Chuyển đổi giá và số lượng thành kiểu double trước khi thực hiện phép nhân
            double price = Double.parseDouble(cartModel.getProductPrice());
            double quantity = Double.parseDouble(cartModel.getTotalQuantity());

            subtotal += price * quantity;
        }

        return subtotal;
    }


    // Phương thức tính tổng giá trị đơn hàng (có thể thêm phí vận chuyển, giảm giá, ...)
    private double calculateTotal(double subtotal) {
        // Placeholder logic for calculating total (có thể thêm phí vận chuyển, giảm giá, ...)
        // Ở đây, ta sử dụng một giả định đơn giản
        return subtotal + 5.0;  // Giả sử có phí vận chuyển là $5
    }

    // BroadcastReceiver để nhận thông báo từ MyCartAdapter khi giá trị tổng thay đổi
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTotalAmount();
        }
    };
}
