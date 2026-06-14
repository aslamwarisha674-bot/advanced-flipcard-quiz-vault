package com.example.flashcardquiz;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ManageActivity extends AppCompatActivity {

    private TextView editorStatusLabel;
    private Button btnAddCard, btnEditCard, btnDeleteCard, btnBackToQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        editorStatusLabel = findViewById(R.id.editorStatusLabel);
        btnAddCard = findViewById(R.id.btnAddCard);
        btnEditCard = findViewById(R.id.btnEditCard);
        btnDeleteCard = findViewById(R.id.btnDeleteCard);
        btnBackToQuiz = findViewById(R.id.btnBackToQuiz);

        updateEditorStatus();

        // ➕ ADD NEW FLASHCARD
        btnAddCard.setOnClickListener(v -> showCardDialog(false));

        // ✏️ EDIT ACTIVE FLASHCARD
        btnEditCard.setOnClickListener(v -> {
            if (MainActivity.flashcardsList.isEmpty()) {
                Toast.makeText(this, "No card available to edit!", Toast.LENGTH_SHORT).show();
            } else {
                showCardDialog(true);
            }
        });

        // 🗑️ DELETE ACTIVE FLASHCARD
        btnDeleteCard.setOnClickListener(v -> {
            if (MainActivity.flashcardsList.isEmpty()) {
                Toast.makeText(this, "No card available to delete!", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete Card " + (MainActivity.currentIndex + 1) + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        MainActivity.flashcardsList.remove(MainActivity.currentIndex);
                        Toast.makeText(this, "Card deleted successfully", Toast.LENGTH_SHORT).show();

                        // Index management to avoid out-of-bounds crashes
                        if (MainActivity.currentIndex >= MainActivity.flashcardsList.size() && !MainActivity.flashcardsList.isEmpty()) {
                            MainActivity.currentIndex = MainActivity.flashcardsList.size() - 1;
                        } else if (MainActivity.flashcardsList.isEmpty()) {
                            MainActivity.currentIndex = 0;
                        }
                        updateEditorStatus();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 🔙 BACK TO QUIZ MODE
        btnBackToQuiz.setOnClickListener(v -> finish());
    }

    private void updateEditorStatus() {
        if (MainActivity.flashcardsList.isEmpty()) {
            editorStatusLabel.setText("Vault is Empty!\n\nClick 'Add' to insert a new tech card into the deck.");
        } else {
            MainActivity.Flashcard current = MainActivity.flashcardsList.get(MainActivity.currentIndex);
            editorStatusLabel.setText("Active Focus: Card " + (MainActivity.currentIndex + 1) + " of " + MainActivity.flashcardsList.size() + "\n\n" +
                    "Q: " + current.question);
        }
    }

    // Common Professional Dialog Box for Add and Edit
    private void showCardDialog(boolean isEditMode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEditMode ? "Edit Current Flashcard" : "Create New Flashcard");

        // Dynamic Layout Generation for Input Fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText inputQuestion = new EditText(this);
        inputQuestion.setHint("Enter Technical Question");
        layout.addView(inputQuestion);

        final EditText inputAnswer = new EditText(this);
        inputAnswer.setHint("Enter Absolute Answer");
        layout.addView(inputAnswer);

        // Pre-fill fields if we are in Edit Mode
        if (isEditMode) {
            MainActivity.Flashcard current = MainActivity.flashcardsList.get(MainActivity.currentIndex);
            inputQuestion.setText(current.question);
            inputAnswer.setText(current.answer);
        }

        builder.setView(layout);

        builder.setPositiveButton(isEditMode ? "Update" : "Save", (dialog, which) -> {
            String q = inputQuestion.getText().toString().trim();
            String a = inputAnswer.getText().toString().trim();

            if (q.isEmpty() || a.isEmpty()) {
                Toast.makeText(this, "Both fields are strictly required!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditMode) {
                // Update existing card data object
                MainActivity.Flashcard current = MainActivity.flashcardsList.get(MainActivity.currentIndex);
                current.question = q;
                current.answer = a;
                Toast.makeText(this, "Card optimized successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Add completely new record to shared data structure
                MainActivity.flashcardsList.add(new MainActivity.Flashcard(q, a));
                Toast.makeText(this, "New card injected into deck", Toast.LENGTH_SHORT).show();
            }
            updateEditorStatus();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}