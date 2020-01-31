package com.apkscanner.plugin.apkcompare;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.apkspectrum.plugin.PlugInManager;
import com.apkspectrum.plugin.PlugInPackage;
import com.apkspectrum.util.SystemUtil;

public class ApkCompareSelecter extends JFrame implements ActionListener {
	private static final long serialVersionUID = -6344550881836031071L;

	JTextField pathField;

	PlugInPackage plugInPackage;

	public ApkCompareSelecter(PlugInPackage plugInPackage) {
		this.plugInPackage = plugInPackage;

		setTitle(plugInPackage.getResourceString("@plugin_name"));
		setIconImage(new ImageIcon(plugInPackage.getIconURL()).getImage());
		setResizable(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(new EmptyBorder(10,10,10,10));
		mainPanel.add(new JLabel(plugInPackage.getResourceString("@no_such_compare")));
		mainPanel.add(Box.createVerticalStrut(10));

		JPanel explorerPanel = new JPanel(new BorderLayout()) {
			private static final long serialVersionUID = -9137863319993837997L;
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                return max;
            }
		};
		explorerPanel.setBorder(new CompoundBorder(new TitledBorder(plugInPackage.getResourceString("@title_set_path")), new EmptyBorder(5,5,5,5)));
		pathField = new JTextField();
		pathField.setEditable(false);
		pathField.setText(plugInPackage.getConfiguration("APK_COMPARE_PATH", ""));
		explorerPanel.add(pathField, BorderLayout.CENTER);
		JButton explorerBtn = new JButton(plugInPackage.getResourceString("@btn_explorer"));
		explorerBtn.setActionCommand("SET_PATH");
		explorerBtn.addActionListener(this);
		explorerPanel.add(explorerBtn, BorderLayout.EAST);
		explorerPanel.setAlignmentX(0f);
		mainPanel.add(explorerPanel);
		mainPanel.add(Box.createVerticalStrut(10));

		JPanel introPanel = new JPanel();
		introPanel.setLayout(new BoxLayout(introPanel, BoxLayout.Y_AXIS));
		introPanel.setBorder(new CompoundBorder(new TitledBorder(plugInPackage.getResourceString("@title_introduce")), new EmptyBorder(5,5,5,5)));
		introPanel.setAlignmentX(.0f);

		try {
			Image preview = ImageIO.read(new URL(plugInPackage.getResourceUri("/ApkComparePreview.png").toString()));
			ImagePanel imagePanel = new ImagePanel(preview);
			imagePanel.setPreferredSize(new Dimension(300,150));
			imagePanel.setAlignmentX(1.0f);
			introPanel.add(imagePanel);
			introPanel.add(Box.createVerticalStrut(10));
		} catch (IOException e) { }

		JTextArea introText = new JTextArea();
		introText.setEditable(false);
		introText.setText(plugInPackage.getResourceString("@compare_intro_desc"));
		introText.setCaretPosition(0);

		JScrollPane textPanel = new JScrollPane(introText);
		textPanel.setPreferredSize(new Dimension(300,50));
		textPanel.setAlignmentX(1.0f);
		introPanel.add(textPanel);
		introPanel.add(Box.createVerticalStrut(10));

		JButton btn = new JButton(plugInPackage.getResourceString("@btn_download"));
		btn.setActionCommand("GO_WEBPAGE");
		btn.addActionListener(this);
		btn.setAlignmentX(1.0f);
		introPanel.add(btn);
		mainPanel.add(introPanel);
		mainPanel.add(Box.createVerticalStrut(10));

		btn = new JButton(plugInPackage.getResourceString("@btn_ok"));
		btn.setActionCommand("CLOSE");
		btn.addActionListener(this);
		JPanel ctrlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		ctrlPanel.add(btn, BorderLayout.EAST);
		ctrlPanel.setAlignmentX(0.0f);
		mainPanel.add(ctrlPanel);

		add(mainPanel, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		switch(evt.getActionCommand()) {
		case "GO_WEBPAGE":
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
		        try {
		            desktop.browse(new URI(plugInPackage.getResourceString("@download_url")));
		        } catch (Exception e1) {
		            e1.printStackTrace();
		        }
		    }
			dispose();
			break;
		case "SET_PATH":
			File file = new File(pathField.getText().trim());
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setDialogType(JFileChooser.OPEN_DIALOG);
			if(SystemUtil.isWindows()) {
				jfc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("APK Compare(.exe)","exe"));
			}
			if(file.exists()) jfc.setSelectedFile(file);
			if(jfc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
				return;
			file = jfc.getSelectedFile();
			if(file.canExecute()) {
				pathField.setText(file.getAbsolutePath());
			} else {

			}
			break;
		case "CLOSE":
			String path = pathField.getText().trim();
			if(new File(path).canExecute()) {
				plugInPackage.setConfiguration("APK_COMPARE_PATH", path);
			}
			PlugInManager.saveProperty();
			dispose();
			break;
		}
	}
}
