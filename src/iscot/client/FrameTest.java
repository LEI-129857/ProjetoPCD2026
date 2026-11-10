package iscot.client;

import iscot.client.gui.ACIcon;
import iscot.client.gui.IrrigationIcon;
import iscot.client.gui.LightIcon;
import iscot.client.gui.SensorControl;
import iscot.client.gui.VentilatorIcon;
import iscot.enums.AirConditioningMode;
import iscot.enums.Areas;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;


public class FrameTest {

    private JFrame frame;

    public static void main(String[] args) {
        FrameTest window = new FrameTest();
		window.open();
    }

    public FrameTest() {
        this.frame = new JFrame("ClientUI");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        addFrameContent();

        frame.pack();

    }
    public void open() {
        frame.setVisible(true);
    }

    public void addFrameContent(){
        frame.setLayout(new GridLayout(4,2));
        JLabel titleLabel = new JLabel("ClientUI");
		frame.add(titleLabel);
		Areas area = Areas.LivingRoom;
        SensorControl humiditySensor = new SensorControl( "💧", "Humidade - " + area, "%",
            0, 100, 1, 50,
            valor -> {
                System.out.println("Mudou para: " + valor);
            });
        frame.add(humiditySensor);

        // painel ventilador
        JPanel painelVentilador = new JPanel();
        painelVentilador.setLayout(new BorderLayout());

        VentilatorIcon ventilator = new VentilatorIcon();
        painelVentilador.add(ventilator, BorderLayout.CENTER);

        JButton btnToggle = new JButton("Ligar Ventilador");

        btnToggle.addActionListener(e -> {
            boolean novoEstado = !ventilator.isAtivo();
            ventilator.setAtivo(novoEstado);
            btnToggle.setText(novoEstado ? "Desligar Ventilador" : "Ligar Ventilador");
            System.out.println("Evento manual: Ventilador -> " + novoEstado);
        });

        painelVentilador.add(btnToggle, BorderLayout.SOUTH);
        frame.add(painelVentilador);
    }
}
