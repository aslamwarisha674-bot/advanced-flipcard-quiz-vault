package com.example.flashcardquiz;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static class Flashcard {
        public String question, answer;
        public Flashcard(String q, String a) {
            this.question = q;
            this.answer = a;
        }
    }

    public static ArrayList<Flashcard> flashcardsList = new ArrayList<>();
    public static int currentIndex = 0;

    private boolean isShowingAnswer = false;
    private int score = 0;
    private int cardsAnsweredCount = 0;

    private TextView cardText, statusLabel, sideLabel, scoreLabel;
    private Button btnFlip, btnPrev, btnNext, btnGoManage, btnRight, btnWrong;
    private LinearLayout cardFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardFrame = findViewById(R.id.cardFrame);
        cardText = findViewById(R.id.cardText);
        statusLabel = findViewById(R.id.statusLabel);
        sideLabel = findViewById(R.id.sideLabel);
        scoreLabel = findViewById(R.id.scoreLabel);
        btnFlip = findViewById(R.id.btnFlip);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnGoManage = findViewById(R.id.btnGoManage);
        btnRight = findViewById(R.id.btnRight);
        btnWrong = findViewById(R.id.btnWrong);

        float scale = getResources().getDisplayMetrics().density;
        cardFrame.setCameraDistance(8000 * scale);

        // Standard Technical Question Set
        if (flashcardsList.isEmpty()) {
            flashcardsList.add(new Flashcard("What is the time complexity of searching in a Hash Map?", "O(1) on average."));
            flashcardsList.add(new Flashcard("What does API stand for?", "Application Programming Interface."));
            flashcardsList.add(new Flashcard("What is the main advantage of Kotlin over Java?", "Null safety features natively."));
            flashcardsList.add(new Flashcard("Which HTTP method is used to update existing data?", "PUT or PATCH."));
            flashcardsList.add(new Flashcard("What is the purpose of Git in software development?", "Version Control System to track changes."));
            flashcardsList.add(new Flashcard("What does SQL stand for?", "Structured Query Language."));
            flashcardsList.add(new Flashcard("What is an Abstract Class in Object Oriented Programming?", "A blueprint class that cannot be instantiated directly."));
            flashcardsList.add(new Flashcard("What does JSON stand for?", "JavaScript Object Notation."));
            flashcardsList.add(new Flashcard("Which architecture design is standard for Android Apps?", "MVVM (Model-View-ViewModel)."));
            flashcardsList.add(new Flashcard("What is the primary role of a Database Primary Key?", "To uniquely identify each record in a table."));
            flashcardsList.add(new Flashcard("What is the default port number for HTTP?", "Port 80."));
            flashcardsList.add(new Flashcard("What does HTML stand for?", "HyperText Markup Language."));
            flashcardsList.add(new Flashcard("What is the purpose of 'Intent' in Android?", "To navigate between activities or pass data."));
            flashcardsList.add(new Flashcard("What is the difference between Array and ArrayList?", "Array has fixed size, ArrayList is resizable."));
            flashcardsList.add(new Flashcard("What does CSS stand for?", "Cascading Style Sheets."));
            flashcardsList.add(new Flashcard("What is the keyword to inherit a class in Java?", "'extends'."));
            flashcardsList.add(new Flashcard("What is the brain of an Android App layout?", "The Activity file (Java/Kotlin)."));
            flashcardsList.add(new Flashcard("What is the purpose of Android Manifest file?", "It contains essential app structure, permissions & screens metadata."));
            flashcardsList.add(new Flashcard("What is Firebase used for?", "Backend-as-a-Service for real-time databases and auth."));
            flashcardsList.add(new Flashcard("What does 'SDK' stand for?", "Software Development Kit."));
        }

        updateCardDisplay();

        btnGoManage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManageActivity.class);
            startActivity(intent);
        });

        btnFlip.setOnClickListener(v -> flipCardWithAnimation());
        btnNext.setOnClickListener(v -> navigate(1));
        btnPrev.setOnClickListener(v -> navigate(-1));

        btnRight.setOnClickListener(v -> {
            score += 10;
            scoreLabel.setText("SCORE: " + score);
            Toast.makeText(this, "Correct! +10 Pts", Toast.LENGTH_SHORT).show();
            checkProgressAndNavigate();
        });

        btnWrong.setOnClickListener(v -> {
            if (score >= 5) score -= 5;
            scoreLabel.setText("SCORE: " + score);
            Toast.makeText(this, "Review Needed! -5 Pts", Toast.LENGTH_SHORT).show();
            checkProgressAndNavigate();
        });
    }

    private void checkProgressAndNavigate() {
        cardsAnsweredCount++;
        if (cardsAnsweredCount >= flashcardsList.size()) {
            showFinalScoreDialog();
        } else {
            navigate(1);
        }
    }

    private void showFinalScoreDialog() {
        int maxPossibleScore = flashcardsList.size() * 10;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🏆 SESSION EVALUATION");
        builder.setMessage("You have reviewed all available tech cards.\n\n" +
                "Total Metrics: " + score + " / " + maxPossibleScore + " Pts");
        builder.setCancelable(false);
        builder.setPositiveButton("Reset Session", (dialog, which) -> {
            score = 0;
            cardsAnsweredCount = 0;
            currentIndex = 0;
            scoreLabel.setText("SCORE: 0");
            isShowingAnswer = false;
            updateCardDisplay();
        });
        builder.setNegativeButton("Close", (dialog, which) -> finish());
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShowingAnswer = false;
        if (currentIndex >= flashcardsList.size() && !flashcardsList.isEmpty()) {
            currentIndex = flashcardsList.size() - 1;
        }
        updateCardDisplay();
    }

    private void navigate(int step) {
        if (!flashcardsList.isEmpty()) {
            currentIndex = (currentIndex + step + flashcardsList.size()) % flashcardsList.size();
            isShowingAnswer = false;
            updateCardDisplay();
        }
    }

    private void flipCardWithAnimation() {
        if (flashcardsList.isEmpty()) return;

        ObjectAnimator flipOut = ObjectAnimator.ofFloat(cardFrame, "rotationY", 0f, 90f);
        flipOut.setDuration(180);
        flipOut.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator flipIn = ObjectAnimator.ofFloat(cardFrame, "rotationY", -90f, 0f);
        flipIn.setDuration(180);
        flipIn.setInterpolator(new AccelerateDecelerateInterpolator());

        flipOut.start();
        cardFrame.postDelayed(() -> {
            isShowingAnswer = !isShowingAnswer;
            updateCardDisplay();
            flipIn.start();
        }, 180);
    }

    private void updateCardDisplay() {
        if (flashcardsList.isEmpty()) {
            cardText.setText("No Flashcards available!\nGo to Manage Vault to seed data.");
            statusLabel.setText("0 OF 0");
            sideLabel.setText("EMPTY");
            btnFlip.setEnabled(false);
            btnRight.setEnabled(false);
            btnWrong.setEnabled(false);
            return;
        }

        btnFlip.setEnabled(true);
        btnRight.setEnabled(true);
        btnWrong.setEnabled(true);

        Flashcard currentCard = flashcardsList.get(currentIndex);
        statusLabel.setText("CARD " + (currentIndex + 1) + " OF " + flashcardsList.size());

        if (isShowingAnswer) {
            cardText.setText(currentCard.answer);
            cardText.setTextColor(android.graphics.Color.parseColor("#388E3C")); // Sophisticated Muted Green
            sideLabel.setText("BACK (ANSWER DECK)");
            sideLabel.setTextColor(android.graphics.Color.parseColor("#388E3C"));
            btnFlip.setText("Hide Answer");
        } else {
            cardText.setText(currentCard.question);
            cardText.setTextColor(android.graphics.Color.parseColor("#FFFFFF"));
            sideLabel.setText("FRONT (QUESTION DECK)");
            sideLabel.setTextColor(android.graphics.Color.parseColor("#1976D2")); // Clean Corporate Blue
            btnFlip.setText("Reveal Answer");
        }
    }
}