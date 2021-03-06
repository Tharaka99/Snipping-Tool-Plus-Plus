package utilities;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import javax.swing.JList;

import main.Main;
import net.miginfocom.swing.MigLayout;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.AbstractListModel;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import data.Preferences;
import editor.Editor;

import javax.swing.ScrollPaneConstants;
import javax.swing.JToggleButton;

import overlay.Overlay;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ImageViewer
{

	private JFrame Viewer;
	private JTextField linkField;
	private JTextField deleteField;
	private ViewerPanel imagePanel;
	private BufferedImage image;
	private JScrollPane scrollPane_1;
	private JToggleButton btnCaptures;
	private JToggleButton btnUploads;
	private JButton btnCopy;
	private JList list;

	private String value = ""; // The selected item text used for grabbed an
								// image

	int correctedIndex = 0;

	private OpenBrowser browser = new OpenBrowser();

	private ButtonGroup bGroup = new ButtonGroup();

	private ArrayList<String> links = new ArrayList<String>();
	private HashMap<String, String> uploadsLinkMap = new HashMap<String, String>();
	private HashMap<String, String> uploadsDeletionMap = new HashMap<String, String>();

	private String imageURL;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					ImageViewer window = new ImageViewer();
					window.Viewer.setVisible(true);
					try
					{
						UIManager
								.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					} catch (Exception e)
					{
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ImageViewer()
	{
		addLinksToList();
		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		Viewer = new JFrame();
		Viewer.addWindowListener(new WindowListener()
		{
			
			@Override
			public void windowOpened(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				
				
			}
			
			@Override
			public void windowClosed(WindowEvent e)
			{
				// TODO Auto-generated method stub
				System.gc();
			}
			
			@Override
			public void windowActivated(WindowEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		});
		Viewer.setIconImage(Toolkit.getDefaultToolkit().getImage(
				ImageViewer.class.getResource("/images/icons/folder.png")));
		Viewer.setTitle("Viewer");
		Viewer.setBounds(100, 100, 612, 458);
		Viewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Viewer.getContentPane().setLayout(new BorderLayout(0, 0));

		final JPanel panel = new JPanel();
		Viewer.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[100.00][298.00,grow]",
				"[28.00][][337.00,grow][][][]"));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, "cell 0 0,grow");

		btnCaptures = new JToggleButton("");
		btnCaptures.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setListModel();
			}
		});
		panel_1.add(btnCaptures);
		btnCaptures.setIcon(new ImageIcon(ImageViewer.class
				.getResource("/images/icons/image_save.png")));

		btnUploads = new JToggleButton("");
		btnUploads.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setListModel();
			}
		});
		panel_1.add(btnUploads);
		btnUploads.setIcon(new ImageIcon(ImageViewer.class
				.getResource("/images/icons/image_upload.png")));

		JSeparator separator_1 = new JSeparator();
		panel.add(separator_1, "cell 0 1 2 1,growx");

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, "cell 0 2,grow");

		list = new JList();
		list.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if (e.getValueIsAdjusting() == false)
				{
					/*
					 * Get the correct image and send to the panel to display
					 */
					if (list.getSelectedIndex() >= 0)
					{
						try
						{
							if (btnUploads.isSelected())
							{
								if (new File(Preferences.DEFAULT_CAPTURE_DIR
										+ "/Uploads/"
										+ list.getSelectedValue().toString()
												.replace(" ", "")) != null)
									image = ImageIO.read(new File(
											Preferences.DEFAULT_CAPTURE_DIR
													+ "/Uploads/"
													+ list.getSelectedValue()
															.toString()
															.replace(" ", "")));
								else
									image = new BufferedImage(250, 250,
											BufferedImage.TYPE_INT_ARGB); // set
																			// it
																			// to
																			// blank
																			// for
																			// now,
																			// no
																			// image
																			// available
							} else
							{
								linkField.setText("");
								deleteField.setText("");
								image = ImageIO.read(new File(
										Preferences.DEFAULT_CAPTURE_DIR
												+ "/Captures/capture("
												+ (list.getSelectedIndex() + 1)
												+ ").png"));
								imageURL = Preferences.DEFAULT_CAPTURE_DIR
										+ "/Captures/capture("
										+ (list.getSelectedIndex() + 1)
										+ ").png";
							}
						} catch (IOException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						imagePanel.setImage(image);
						scrollPane_1.setPreferredSize(new Dimension(image
								.getWidth(), image.getHeight()));
						scrollPane_1.setViewportView(imagePanel);
						// Viewer.setSize(Viewer.getWidth()+1,
						// Viewer.getHeight());
						if (btnUploads.isSelected())
							setLinks(); // set the links
					}
				}
			}

			private void setLinks()
			{
				value = list.getSelectedValue().toString().replace(" ", "");
				if (uploadsLinkMap.get(value) == null)
				{
					linkField.setText("LINK DATA UNAVAILABLE");
					deleteField.setText("LINK DATA UNAVAILABLE");
				} else
				{
					linkField.setText(uploadsLinkMap.get(value));
					deleteField.setText(uploadsDeletionMap.get(value));
				}
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(list);

		imagePanel = new ViewerPanel();

		scrollPane_1 = new JScrollPane();
		scrollPane_1
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_1
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane_1.getHorizontalScrollBar().setUnitIncrement(16);
		panel.add(scrollPane_1, "cell 1 2,grow");

		JSeparator separator = new JSeparator();
		panel.add(separator, "cell 0 3 2 1,growx");

		JLabel lblLink = new JLabel("Link:");
		panel.add(lblLink, "cell 0 4,alignx trailing");

		linkField = new JTextField();
		linkField.setEditable(false);
		panel.add(linkField, "cell 1 4,growx");
		linkField.setColumns(10);

		JLabel lblDeletionLink = new JLabel("Deletion Link:");
		panel.add(lblDeletionLink, "cell 0 5,alignx trailing");

		deleteField = new JTextField();
		deleteField.setEditable(false);
		panel.add(deleteField, "cell 1 5,growx");
		deleteField.setColumns(10);

		JButton btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (btnUploads.isSelected())
						OpenBrowser.open(new URI(linkField.getText()));
					else
						Desktop.getDesktop().open(new File(imageURL));

				} catch (URISyntaxException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel.add(btnOpen, "flowx,cell 1 0");

		btnCopy = new JButton("Copy");
		btnCopy.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ClipboardUtilities.setClipboardImage(image);
				Main.displayInfoMessage("Copied to Clipboard", list
						.getSelectedValue().toString() + " has been copied.");
			}
		});
		btnCopy.setToolTipText("Copies image to clipboard");
		panel.add(btnCopy, "cell 1 0");

		JButton btnSendToEditor = new JButton("Send to Editor");
		btnSendToEditor.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				BufferedImage temp = new BufferedImage(image.getWidth(), image
						.getHeight(), image.getType());
				temp.setData(image.getData());
				new Editor(temp, Overlay.UPLOAD);
			}
		});
		panel.add(btnSendToEditor, "cell 1 0");

		bGroup.add(btnCaptures);
		bGroup.add(btnUploads);
		btnUploads.setSelected(true);

		setListModel();
		Viewer.setVisible(true);
		btnCaptures.setFocusable(false);
		btnUploads.setFocusable(false);
	}

	private void addLinksToList()
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					Preferences.DEFAULT_CAPTURE_DIR
							+ "/Uploads/imgur_links.txt")));
			String line;
			String parsedLine[];
			int skip = 2;

			while ((line = reader.readLine()) != null)
			{
				if (skip == 0)
				{
					parsedLine = line.split(" - ");
					uploadsLinkMap.put(parsedLine[0] + ".png", parsedLine[1]);
					uploadsDeletionMap.put(parsedLine[0] + ".png",
							parsedLine[2]);
				} else
					skip--;
			}

		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void setListModel()
	{
		final ArrayList<String> items = new ArrayList<String>();
		if (btnUploads.isSelected()) // get uploads
		{
			File folder = new File(Preferences.DEFAULT_CAPTURE_DIR
					+ "/Uploads/");
			File[] listOfFiles = folder.listFiles();
			Arrays.sort(listOfFiles, new Comparator<File>()
			{
				@Override
				public int compare(File f1, File f2)
				{
					return Long.valueOf(f1.lastModified()).compareTo(
							f2.lastModified());
				}
			});

			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].getName().contains("upload("))
				{
					items.add(listOfFiles[i].getName());
				}
			}
		} else
		{
			/*
			 * 
			 * REDO THIS JUST LIKE DONE ABOVE
			 */
			int index = 0;
			File file;

			do
			{
				index++;
				file = new File(Preferences.DEFAULT_CAPTURE_DIR
						+ "/Captures/capture(" + index + ").png");
				items.add(file.getName());
			} while (file.exists());

		}
		list.setModel(new AbstractListModel()
		{

			@Override
			public int getSize()
			{
				return items.size();
			}

			@Override
			public Object getElementAt(int index)
			{
				return items.get(index);
			}
		});
		list.setSelectedIndex(0);
	}
}
