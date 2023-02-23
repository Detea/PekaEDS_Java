package pk.pekaeds.ui.settings;

import net.miginfocom.swing.MigLayout;
import pk.pekaeds.settings.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

// TODO Cleanup: This class can be cleaned up quite a bit. However, it works and this code isn't that important.
public final class PanelMapProfile extends JPanel {
    private JList<String> lstScrollingTypes;
    private JList<String> lstWeatherTypes;
    
    private DefaultListModel<String> modelScrolling;
    private DefaultListModel<String> modelWeather;
    
    private JButton btnAddScrollingType;
    private JButton btnAddWeatherType;
    private JButton btnRemoveScrollingType;
    private JButton btnRemoveWeatherType;
    
    private JButton btnResetScrollingTypes;
    private JButton btnResetWeatherTypes;
    
    private List<String> stagedScrollingTypes = new ArrayList<>();
    private List<String> stagedWeatherTypes = new ArrayList<>();
    
    public PanelMapProfile() {
        setup();
    }
    private void setup() {
        modelScrolling = new DefaultListModel<>();
        modelWeather = new DefaultListModel<>();
        
        lstScrollingTypes = new JList<>(modelScrolling);
        lstWeatherTypes = new JList<>(modelWeather);
        
        btnAddScrollingType = new JButton("Add");
        btnRemoveScrollingType = new JButton("Remove");
        btnResetScrollingTypes = new JButton("Reset");
    
        btnAddWeatherType = new JButton("Add");
        btnRemoveWeatherType = new JButton("Remove");
        btnResetWeatherTypes = new JButton("Reset");
        
        setBorder(BorderFactory.createTitledBorder("Map profile"));
        
        var pScrollingButtons = new JPanel();
        pScrollingButtons.setLayout(new MigLayout());
        pScrollingButtons.add(btnAddScrollingType);
        pScrollingButtons.add(btnRemoveScrollingType);
        pScrollingButtons.add(btnResetScrollingTypes);
    
        var lblScrolling = new JLabel("Scrolling:");
        var spScrolling = new JScrollPane(lstScrollingTypes);
        
        var pScrolling = new JPanel();
        pScrolling.setLayout(new MigLayout());
        pScrolling.add(lblScrolling, "dock north");
        pScrolling.add(spScrolling, "dock center");
        pScrolling.add(pScrollingButtons, "dock south");
        
        var pWeatherButtons = new JPanel();
        pWeatherButtons.setLayout(new MigLayout());
        pWeatherButtons.add(btnAddWeatherType);
        pWeatherButtons.add(btnRemoveWeatherType);
        pWeatherButtons.add(btnResetWeatherTypes);
        
        var lblWeather = new JLabel("Weather:");
        var spWeather = new JScrollPane(lstWeatherTypes);
        
        var pWeather = new JPanel();
        pWeather.setLayout(new MigLayout());
        pWeather.add(lblWeather, "dock north");
        pWeather.add(spWeather, "dock center");
        pWeather.add(pWeatherButtons, "dock south");
        
        setLayout(new MigLayout("", "fill", "fill"));
        add(pScrolling, "cell 0 0, height 100%");
        add(pWeather, "cell 1 0, height 100%");
    
        lstScrollingTypes.setMaximumSize(new Dimension(lstScrollingTypes.getWidth(), 450));
        lstWeatherTypes.setMaximumSize(new Dimension(lstWeatherTypes.getWidth(), 450));
        
        btnAddScrollingType.addActionListener(new AddToListAction(modelScrolling, stagedScrollingTypes));
        btnAddWeatherType.addActionListener(new AddToListAction(modelWeather, stagedWeatherTypes));
        
        btnRemoveScrollingType.addActionListener(new RemoveFromListAction(lstScrollingTypes));
        btnRemoveWeatherType.addActionListener(new RemoveFromListAction(lstWeatherTypes));
        
        btnResetScrollingTypes.addActionListener(new ResetListAction(modelScrolling, Settings.getMapProfile().getDefaultScrollingTypes()));
        btnResetWeatherTypes.addActionListener(new ResetListAction(modelWeather, Settings.getMapProfile().getDefaultWeatherTypes()));
    }
    
    DefaultListModel<String> getScrollingTypes() {
        return modelScrolling;
    }
    
    DefaultListModel<String> getWeatherTypes() {
        return modelWeather;
    }
    
    public void resetValues() {
        modelScrolling.removeAllElements();
        modelWeather.removeAllElements();
        
        modelScrolling.addAll(Settings.getMapProfile().getScrollingTypes());
        modelWeather.addAll(Settings.getMapProfile().getWeatherTypes());
        // TODO I'm to tired for this shit. Fix the fucking stagedshit not being loaded and whatever
    }
    
    private class AddToListAction extends AbstractAction {
        private DefaultListModel<String> model;
        private List<String> dataList;
        
        public AddToListAction(DefaultListModel<String> listModel, List<String> data) {
            this.model = listModel;
            this.dataList = data;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            var res = JOptionPane.showInputDialog("Add a value:");
            
            if (!res.isBlank()) {
                model.addElement(res);
                dataList.add(res);
            }
        }
    }
    
    private class RemoveFromListAction extends AbstractAction {
        private JList<String> list;

        public RemoveFromListAction(JList<String> jlist) {
            this.list = jlist;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            int index = list.getSelectedIndex();
            ((DefaultListModel<String>) list.getModel()).remove(index);
        }
    }
    
    private class ResetListAction extends AbstractAction {
        private DefaultListModel<String> jlist;
        private List<String> data;
        
        public ResetListAction(DefaultListModel<String> list, List<String> dataList) {
            this.jlist = list;
            this.data = dataList;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            jlist.removeAllElements();
            jlist.addAll(data);
        }
    }
}
