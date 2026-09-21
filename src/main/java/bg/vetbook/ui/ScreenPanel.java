package bg.vetbook.ui;

import javax.swing.*;

// Общ родител на четирите екрана.
public abstract class ScreenPanel extends JPanel {

    // Името на екрана. Изписва се на бутона отляво.
    public abstract String getTitle();

    // Вика се всеки път, когато екранът се отвори.
    // Екраните с таблици го презаписват, за да презаредят данните си.
    public void onShown() {
    }
}
