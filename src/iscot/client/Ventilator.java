package iscot.client;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import iscot.client.gui.VentilatorIcon;
import iscot.enums.Areas;
import iscot.events.AreaVentilationOrderEvent;

public class Ventilator extends Thread {
    private final CorretorMensagens broker;
    private final Areas area;
    private VentilatorIcon ventilator;

    public Ventilator(CorretorMensagens broker, Areas area) {
        this.broker = broker;
        this.area = area;
        initGUI();
    }

    private void initGUI() {
        JFrame janela = new JFrame("Ventilador em " + area);
        janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel painel = new JPanel(new BorderLayout());
        ventilator = new VentilatorIcon();
        painel.add(ventilator, BorderLayout.CENTER);

        janela.setContentPane(painel);
        janela.setLocation(600, 400);
        janela.pack();
        janela.setVisible(true);
    }

    @Override
    public void run() {
        try {
            while (true) {
                // TODO (fase 4/5): receber as ordens do servidor em vez da mesa local.
                Object event = broker.get();

                if (event instanceof AreaVentilationOrderEvent) {
                    AreaVentilationOrderEvent e = (AreaVentilationOrderEvent) event;
                    if (e.getArea() == this.area) {
                        SwingUtilities.invokeLater(() -> ventilator.setLigado(e.isActionOn()));
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}