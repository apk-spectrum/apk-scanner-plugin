package com.apkscanner.plugin.apkcompare;

import java.awt.EventQueue;
import java.io.File;

import com.apkspectrum.data.apkinfo.ApkInfo;
import com.apkspectrum.plugin.AbstractExternalTool;
import com.apkspectrum.plugin.PlugInManager;
import com.apkspectrum.plugin.PlugInPackage;
import com.apkspectrum.plugin.manifest.Component;
import com.apkspectrum.util.ConsolCmd;
import com.apkspectrum.util.Log;
import com.apkspectrum.util.SystemUtil;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class ApkCompareLinker extends AbstractExternalTool {

	private static String comparePath;

	public ApkCompareLinker(PlugInPackage pluginPackage, Component component) {
		super(pluginPackage, component);
	}

	@Override
	public void launch() {
		ApkInfo info = PlugInManager.getApkInfo();
		if(info == null) return;
		launch(info.filePath);
	}

	@Override
	public void launch(final String src) {
		Log.v("launch " + src);
		if(src == null) return;
		comparePath = getApkComparePath();
		if(comparePath != null) {
			savePath();
			Thread t = new Thread(new Runnable() {
				public void run() {
					ConsolCmd.exec(new String[] {comparePath, src}, true);
				}
			});
			t.setPriority(Thread.NORM_PRIORITY);
			t.start();
		} else {
			Log.v("no such compare ");
			showPopup();
		}
	}

	@Override
	public void launch(final String src1, final String src2) {
		Log.v("launch " + src1 + ", " + src2);
		comparePath = getApkComparePath();
		if(comparePath != null) {
			savePath();
			Thread t = new Thread(new Runnable() {
				public void run() {
					ConsolCmd.exec(new String[] {comparePath, src1, src2}, true);
				}
			});
			t.setPriority(Thread.NORM_PRIORITY);
			t.start();
		} else {
			Log.v("no such compare");
			showPopup();
		}
	}

	private String getApkComparePath() {
		if(comparePath != null && new File(comparePath).canExecute())
			return comparePath;
		String comparePath = getPlugInConfig().getConfiguration("APK_COMPARE_PATH");
		if(comparePath != null && new File(comparePath).canExecute()) {
			return comparePath;
		}
		if(SystemUtil.isWindows()) {
			String[][] HKLM_keys = {
				{"SOFTWARE\\Wow6432Node\\APK Compare", ""},
				{"SOFTWARE\\APK Compare", ""},
				{"SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\APK Compare", ""},
				{"SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\APK Compare", ""},
				{"SOFTWARE\\Classes\\CLSID\\{FCF5634A-6021-4E70-A895-4D21422BCF93}\\InprocServer32", "UninstallString"}
			};
			for(String[] key: HKLM_keys) {
				if(Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, key[0])
						&& Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, key[0], key[1])) {
					comparePath = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, key[0], key[1]);
					if(comparePath == null || comparePath.isEmpty()) continue;
					if(comparePath.endsWith(".exe") || comparePath.endsWith(".dll") || comparePath.endsWith("\\")) {
						comparePath = comparePath.substring(0, comparePath.lastIndexOf("\\"));
					}
					comparePath += "\\ApkCompare.exe";
					if(new File(comparePath).canExecute()) {
						return comparePath;
					}
				}
			}
			String[] assumePath = {
				"C:\\Program Files\\APKCompare\\ApkCompare.exe",
				"C:\\Program Files (x86)\\APKCompare\\ApkCompare.exe"
			};
			for(String path: assumePath) {
				if(new File(path).canExecute()) {
					return path;
				}
			}
		} else if(SystemUtil.isLinux()) {
			comparePath = "/opt/APKCompare/APKCompare.sh";
			if(new File(comparePath).canExecute()) {
				return comparePath;
			}
		}
		return null;
	}

	private void savePath() {
		String path = getPlugInPackage().getConfiguration("APK_COMPARE_PATH", "");
		if(!path.contentEquals(comparePath)) {
			getPlugInPackage().setConfiguration("APK_COMPARE_PATH", comparePath);
			PlugInManager.saveProperty();
		}
	}

	private void showPopup() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ApkCompareSelecter(getPlugInPackage()).setVisible(true);
			}
		});
	}
}
