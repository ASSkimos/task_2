package ui;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;


public class OptionPanel extends JPanel {


    private CheckersWindow window;

    private JButton restartBtn;


    public OptionPanel(CheckersWindow window) {
        super(new GridLayout(0, 1));

        this.window = window;

        // Инициализация основных компонентов
        OptionListener ol = new OptionListener();
        this.restartBtn = new JButton("Restart");
        this.restartBtn.addActionListener(ol);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel middle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(restartBtn);
        this.add(top);
        this.add(middle);
        this.add(bottom);
    }


    private class OptionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (window == null) {
                return;
            }
            Object src = e.getSource();
            if (src == restartBtn) {
                window.restart();
            }

        }
    }
}
