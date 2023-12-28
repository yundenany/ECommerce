package com.example.ecommerce.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ecommerce.R;
import com.razorpay.Checkout;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    double amount = 0.0;
    Toolbar toolbar;
    TextView subTotal, total; // Added declaration for subTotal and total
    Button paymentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Checkout.preload(getApplicationContext());
        setContentView(R.layout.activity_payment);

        // ...

        amount = getIntent().getDoubleExtra("amount", 0.0);

        subTotal = findViewById(R.id.sub_total);
        total = findViewById(R.id.total_amt);
        paymentBtn = findViewById(R.id.pay_btn);

        // Format amount to currency for sub_total
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String formattedSubTotal = currencyFormat.format(amount);
        subTotal.setText(formattedSubTotal);

        // Set total amount to the same as the original amount
        String formattedTotal = currencyFormat.format(amount);
        total.setText(formattedTotal);


        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mô phỏng hành động "Check Out" - không có thanh toán thực tế
                simulateCheckout();
            }
        });
    }

    private void simulateCheckout() {
        // Đoạn mã này được thực hiện khi người dùng nhấn vào nút "Check Out"
        // Ở đây, chúng ta chỉ hiển thị thông báo và không thực hiện thanh toán thực tế
        Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();

        // Quay trở lại màn hình chính
        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Xóa ngăn xếp hoạt động
        startActivity(intent);
        finish();
    }
}
