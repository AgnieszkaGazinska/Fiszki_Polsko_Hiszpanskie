
package frontend;

import backend.BazaDanych;
import frontend.StartFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class ModyfikujFrame extends javax.swing.JFrame {
    Map<String, Integer> mapaKategorii = new HashMap<>();
    private DefaultTableModel modelTabeli;
    
    
    public ModyfikujFrame() {
        initComponents();
        setTitle("Fiszki - modyfikuj");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        //wypełnianie comboboxów
        wypelnijCmb(cmbKategoriaSlowko);
        wypelnijCmb(cmbUsunKat);
        wypelnijCmb(cmbEdytujKat);
        wypelnijCmb(cmbZaladujKat);
        
        //inicjalizacja modelu tabeli
        String[] kolumny = {"Polski", "Hiszpański", "Edytuj", "Usuń"};
        modelTabeli = new DefaultTableModel(null, kolumny);
        tblTabela.setModel(modelTabeli);
        
        //Wyśrodkowanie nazw kolumn tabeli
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) tblTabela.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        //scrollpane
        scrTabelaScroll.setViewportView(tblTabela);
        scrTabelaScroll.setPreferredSize(new Dimension(460, 430));
        pnlTabela.setLayout(new BorderLayout());
        add(pnlTabela, BorderLayout.CENTER);
        
        tblTabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int wiersz = tblTabela.rowAtPoint(e.getPoint());
                int kolumna = tblTabela.columnAtPoint(e.getPoint());
                
                String polski = (String) modelTabeli.getValueAt(wiersz, 0);
                String hiszpanski = (String) modelTabeli.getValueAt(wiersz, 1);
                int kategoriaId = mapaKategorii.get((String) cmbZaladujKat.getSelectedItem());

                if (kolumna == 2) {
                    edytujSlowko(wiersz, polski, hiszpanski, kategoriaId);
                } else if (kolumna == 3) {
                    usunSlowko(wiersz, polski, kategoriaId);
                }
            }
        });
        setVisible(true);
}
    
    //wypelnianie comboboxów
    private void wypelnijCmb(JComboBox<String> comboBox) {
            comboBox.removeAllItems(); // Czyszczenie zawartości
            mapaKategorii = BazaDanych.pobierzKategorie();

            if (mapaKategorii != null && !mapaKategorii.isEmpty()) {
                for (String nazwa : mapaKategorii.keySet()) {
                    comboBox.addItem(nazwa);
                }
                comboBox.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Brak kategorii. Proszę dodać kategorię.", "Informacja", JOptionPane.WARNING_MESSAGE);
            }
    }

    //ładowanie i odświeżanie słów
    private void zaladujSlowka() {
        try {
            modelTabeli.setRowCount(0);
            
            String wybranaKategoria = (String) cmbZaladujKat.getSelectedItem();
            if (wybranaKategoria == null) {
                JOptionPane.showMessageDialog(this, "Wybierz kategorię!", "Błąd", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer kategoriaId = mapaKategorii.get(wybranaKategoria);
            if (kategoriaId == null) {
                return;
            }
            Map<String, String> slowka = BazaDanych.pobierzSlowka(kategoriaId);
            if (slowka.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Brak słówek w tej kategorii.", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (Map.Entry<String, String> entry : slowka.entrySet()) {
                modelTabeli.addRow(new Object[]{entry.getKey(), entry.getValue(), "Edytuj", "Usuń"});
            }
                        
            modelTabeli.fireTableDataChanged();
            
            tblTabela.setPreferredScrollableViewportSize(new Dimension(tblTabela.getPreferredSize().height, 2000));
            
            scrTabelaScroll.setViewportView(tblTabela);
            revalidate();  // Odświeżenie układu
            repaint();  // Przeładuj widok

            JOptionPane.showMessageDialog(this, "Załadowano słówka!", "Sukces", JOptionPane.INFORMATION_MESSAGE);   
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas ładowania słówek: " + ex.getMessage());
        }  
    }
        
    private void dodajSlowko() {
        try {
            String polski = tfPolski.getText().trim();
            String hiszpanski = tfHiszpanski.getText().trim();
            String wybranaKategoria = (String) cmbKategoriaSlowko.getSelectedItem();

            if (wybranaKategoria == null || polski.isEmpty() || hiszpanski.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Wypełnij wszystkie pola!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int kategoriaId = mapaKategorii.get(wybranaKategoria);
            BazaDanych.dodajSlowko(polski, hiszpanski, kategoriaId);
            zaladujSlowka();
            JOptionPane.showMessageDialog(this, "Dodano słówko!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Błąd: " + ex.getMessage());
        }
    }
    
    private void edytujSlowko(int wiersz, String polski, String hiszpanski, int kategoriaId) {
        try {
            String nowyPolski = JOptionPane.showInputDialog(this, "Nowe słowo po polsku:", polski);
            String nowyHiszpanski = JOptionPane.showInputDialog(this, "Nowe słowo po hiszpańsku:", hiszpanski);

            if (nowyPolski != null && nowyHiszpanski != null && !nowyPolski.trim().isEmpty() && !nowyHiszpanski.trim().isEmpty()) {
                BazaDanych.edytujSlowko(wiersz, nowyPolski, nowyHiszpanski, kategoriaId);
                zaladujSlowka();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas edycji: " + ex.getMessage());
        }
    }

    private void usunSlowko(int wiersz, String polski, int kategoriaId) {
        try {
            int potwierdzenie = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz usunąć: " + polski + "?", "Potwierdzenie", JOptionPane.YES_NO_OPTION);
            if (potwierdzenie == JOptionPane.YES_OPTION) {
                BazaDanych.usunSlowko(polski, kategoriaId);
                zaladujSlowka();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas usuwania: " + ex.getMessage());
        }
    }

    private void odswiezKategorie() {
        try {
            Map<String, Integer> kategorie = BazaDanych.pobierzKategorie();
            for (String kategoria : kategorie.keySet()) {
                cmbUsunKat.addItem(kategoria);
        }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas ładowania kategorii: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    
        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlOpcje = new javax.swing.JPanel();
        pnlDodajKat = new javax.swing.JPanel();
        lblDodajKat = new javax.swing.JLabel();
        btnDodajKat = new javax.swing.JButton();
        tfDodajKat = new javax.swing.JTextField();
        pnlUsunKat = new javax.swing.JPanel();
        lblUsunKat = new javax.swing.JLabel();
        cmbUsunKat = new javax.swing.JComboBox<>();
        btnUsunKat = new javax.swing.JButton();
        pnlEdytujKat = new javax.swing.JPanel();
        lblEdytujKat = new javax.swing.JLabel();
        cmbEdytujKat = new javax.swing.JComboBox<>();
        tfEdytuj = new javax.swing.JTextField();
        btnEdytujKat = new javax.swing.JButton();
        pnlDodajSlowko = new javax.swing.JPanel();
        tfPolski = new javax.swing.JTextField();
        cmbKategoriaSlowko = new javax.swing.JComboBox<>();
        lblSlowko = new javax.swing.JLabel();
        tfHiszpanski = new javax.swing.JTextField();
        btnDodajSlowko = new javax.swing.JButton();
        lblPolski = new javax.swing.JLabel();
        lblHiszpanski = new javax.swing.JLabel();
        lblKategoria = new javax.swing.JLabel();
        pnlZaladuj = new javax.swing.JPanel();
        lblZaladujKat = new javax.swing.JLabel();
        cmbZaladujKat = new javax.swing.JComboBox<>();
        btnZaladuj = new javax.swing.JButton();
        pnlTabela = new javax.swing.JPanel();
        scrTabelaScroll = new javax.swing.JScrollPane();
        tblTabela = new javax.swing.JTable();
        btnOdswiez = new javax.swing.JButton();
        mbrModyfikuj = new javax.swing.JMenuBar();
        mnuStronaGlowna = new javax.swing.JMenu();
        mniStronaGlowna = new javax.swing.JMenuItem();
        mnuPomoc = new javax.swing.JMenu();
        mniPomoc = new javax.swing.JMenuItem();
        mnuWyjscie = new javax.swing.JMenu();
        mniWyjdz = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnlDodajKat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblDodajKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblDodajKat.setText("Dodaj kategorię:");

        btnDodajKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDodajKat.setText("Dodaj");
        btnDodajKat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDodajKatActionPerformed(evt);
            }
        });

        tfDodajKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout pnlDodajKatLayout = new javax.swing.GroupLayout(pnlDodajKat);
        pnlDodajKat.setLayout(pnlDodajKatLayout);
        pnlDodajKatLayout.setHorizontalGroup(
            pnlDodajKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDodajKatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDodajKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDodajKat, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlDodajKatLayout.createSequentialGroup()
                        .addComponent(tfDodajKat, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDodajKat, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlDodajKatLayout.setVerticalGroup(
            pnlDodajKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDodajKatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDodajKat)
                .addGap(3, 3, 3)
                .addGroup(pnlDodajKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfDodajKat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDodajKat, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlUsunKat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblUsunKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblUsunKat.setText("Usuń kategorię:");

        cmbUsunKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbUsunKat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnUsunKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnUsunKat.setText("Usuń");
        btnUsunKat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsunKatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlUsunKatLayout = new javax.swing.GroupLayout(pnlUsunKat);
        pnlUsunKat.setLayout(pnlUsunKatLayout);
        pnlUsunKatLayout.setHorizontalGroup(
            pnlUsunKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUsunKatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlUsunKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUsunKat, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlUsunKatLayout.createSequentialGroup()
                        .addComponent(cmbUsunKat, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnUsunKat, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlUsunKatLayout.setVerticalGroup(
            pnlUsunKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlUsunKatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUsunKat)
                .addGap(7, 7, 7)
                .addGroup(pnlUsunKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnUsunKat, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(cmbUsunKat))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlEdytujKat.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblEdytujKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblEdytujKat.setText("Edytuj nazwę kategorii:");

        cmbEdytujKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbEdytujKat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        tfEdytuj.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnEdytujKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnEdytujKat.setText("Edytuj");
        btnEdytujKat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEdytujKatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlEdytujKatLayout = new javax.swing.GroupLayout(pnlEdytujKat);
        pnlEdytujKat.setLayout(pnlEdytujKatLayout);
        pnlEdytujKatLayout.setHorizontalGroup(
            pnlEdytujKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEdytujKatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEdytujKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEdytujKat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlEdytujKatLayout.createSequentialGroup()
                        .addGroup(pnlEdytujKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbEdytujKat, 0, 228, Short.MAX_VALUE)
                            .addComponent(tfEdytuj))
                        .addGap(18, 18, 18)
                        .addComponent(btnEdytujKat, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlEdytujKatLayout.setVerticalGroup(
            pnlEdytujKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEdytujKatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEdytujKat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbEdytujKat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(pnlEdytujKatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfEdytuj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEdytujKat))
                .addContainerGap())
        );

        pnlDodajSlowko.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        tfPolski.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        cmbKategoriaSlowko.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbKategoriaSlowko.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblSlowko.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSlowko.setText("Dodaj słówko:");

        tfHiszpanski.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        btnDodajSlowko.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnDodajSlowko.setText("Dodaj");
        btnDodajSlowko.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDodajSlowkoActionPerformed(evt);
            }
        });

        lblPolski.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblPolski.setText("Polski:");

        lblHiszpanski.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblHiszpanski.setText("Hiszpański:");

        lblKategoria.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblKategoria.setText("Kategoria:");

        javax.swing.GroupLayout pnlDodajSlowkoLayout = new javax.swing.GroupLayout(pnlDodajSlowko);
        pnlDodajSlowko.setLayout(pnlDodajSlowkoLayout);
        pnlDodajSlowkoLayout.setHorizontalGroup(
            pnlDodajSlowkoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDodajSlowkoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDodajSlowkoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDodajSlowkoLayout.createSequentialGroup()
                        .addComponent(lblSlowko, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlDodajSlowkoLayout.createSequentialGroup()
                        .addGroup(pnlDodajSlowkoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlDodajSlowkoLayout.createSequentialGroup()
                                .addComponent(tfPolski, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(tfHiszpanski, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlDodajSlowkoLayout.createSequentialGroup()
                                .addComponent(lblPolski, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblHiszpanski, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblKategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlDodajSlowkoLayout.createSequentialGroup()
                                .addComponent(cmbKategoriaSlowko, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addComponent(btnDodajSlowko, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        pnlDodajSlowkoLayout.setVerticalGroup(
            pnlDodajSlowkoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDodajSlowkoLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblSlowko)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDodajSlowkoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPolski)
                    .addComponent(lblHiszpanski))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDodajSlowkoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfPolski, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfHiszpanski, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblKategoria)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDodajSlowkoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbKategoriaSlowko, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDodajSlowko))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlZaladuj.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblZaladujKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblZaladujKat.setText("Załaduj kategorię:");

        cmbZaladujKat.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbZaladujKat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnZaladuj.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnZaladuj.setText("Załaduj");
        btnZaladuj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZaladujActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlZaladujLayout = new javax.swing.GroupLayout(pnlZaladuj);
        pnlZaladuj.setLayout(pnlZaladujLayout);
        pnlZaladujLayout.setHorizontalGroup(
            pnlZaladujLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlZaladujLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblZaladujKat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbZaladujKat, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnZaladuj, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlZaladujLayout.setVerticalGroup(
            pnlZaladujLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlZaladujLayout.createSequentialGroup()
                .addGap(0, 18, Short.MAX_VALUE)
                .addGroup(pnlZaladujLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblZaladujKat)
                    .addComponent(cmbZaladujKat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnZaladuj))
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout pnlOpcjeLayout = new javax.swing.GroupLayout(pnlOpcje);
        pnlOpcje.setLayout(pnlOpcjeLayout);
        pnlOpcjeLayout.setHorizontalGroup(
            pnlOpcjeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOpcjeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlOpcjeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pnlDodajSlowko, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlOpcjeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(pnlEdytujKat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlZaladuj, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlDodajKat, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlUsunKat, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 223, Short.MAX_VALUE))
        );
        pnlOpcjeLayout.setVerticalGroup(
            pnlOpcjeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlOpcjeLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(pnlZaladuj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDodajKat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlUsunKat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(pnlEdytujKat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDodajSlowko, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnlTabela.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlTabela.setMaximumSize(new java.awt.Dimension(450, 450));
        pnlTabela.setLayout(new javax.swing.BoxLayout(pnlTabela, javax.swing.BoxLayout.LINE_AXIS));

        scrTabelaScroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrTabelaScroll.setMaximumSize(new java.awt.Dimension(450, 450));

        tblTabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblTabela.setPreferredSize(new java.awt.Dimension(450, 2000));
        tblTabela.setRowHeight(15);
        scrTabelaScroll.setViewportView(tblTabela);

        pnlTabela.add(scrTabelaScroll);

        btnOdswiez.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOdswiez.setText("Odśwież dane");
        btnOdswiez.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOdswiezActionPerformed(evt);
            }
        });

        mnuStronaGlowna.setText("Strona główna");

        mniStronaGlowna.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniStronaGlowna.setText("Przejdź do strony głównej");
        mniStronaGlowna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniStronaGlownaActionPerformed(evt);
            }
        });
        mnuStronaGlowna.add(mniStronaGlowna);

        mbrModyfikuj.add(mnuStronaGlowna);

        mnuPomoc.setText("Pomoc");

        mniPomoc.setText("Pomoc");
        mniPomoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPomocActionPerformed(evt);
            }
        });
        mnuPomoc.add(mniPomoc);

        mbrModyfikuj.add(mnuPomoc);

        mnuWyjscie.setText("Wyjście");

        mniWyjdz.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        mniWyjdz.setText("Wyjdź");
        mniWyjdz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniWyjdzActionPerformed(evt);
            }
        });
        mnuWyjscie.add(mniWyjdz);

        mbrModyfikuj.add(mnuWyjscie);

        setJMenuBar(mbrModyfikuj);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(pnlTabela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlOpcje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnOdswiez)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnOdswiez)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlOpcje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlTabela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mniWyjdzActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniWyjdzActionPerformed
        System.exit(0);
    }//GEN-LAST:event_mniWyjdzActionPerformed

    private void mniStronaGlownaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniStronaGlownaActionPerformed
        new StartFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_mniStronaGlownaActionPerformed

    private void btnZaladujActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZaladujActionPerformed
        zaladujSlowka();
    }//GEN-LAST:event_btnZaladujActionPerformed

    private void btnDodajKatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDodajKatActionPerformed
        String nazwaKategorii = tfDodajKat.getText().trim();
        
        if (nazwaKategorii.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nazwa kategorii nie może być pusta!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Wywołanie metody, która doda kategorię do bazy danych
            BazaDanych.dodajKategorie(nazwaKategorii);
            odswiezKategorie();
            JOptionPane.showMessageDialog(this, "Kategoria została dodana!", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            tfDodajKat.setText("");  // Wyczyść pole tekstowe po dodaniu kategorii
            // Możesz opcjonalnie dodać metodę do odświeżenia widoku kategorii w aplikacji
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Błąd bazy danych: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDodajKatActionPerformed

    private void btnUsunKatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsunKatActionPerformed
        try {
                String wybranaKategoria = (String) cmbUsunKat.getSelectedItem();
                if (wybranaKategoria == null) {
                    JOptionPane.showMessageDialog(this, "Wybierz kategorię do usunięcia!", "Błąd", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Map<String, Integer> kategorie = BazaDanych.pobierzKategorie();
                Integer kategoriaId = kategorie.get(wybranaKategoria);

                if (kategoriaId == null) {
                    JOptionPane.showMessageDialog(this, "Nie znaleziono ID kategorii!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BazaDanych.usunKategorie(wybranaKategoria);  // Zakładając, że masz taką metodę w BazaDanych

                cmbUsunKat.removeItem(wybranaKategoria);
                JOptionPane.showMessageDialog(this, "Kategoria została usunięta.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Błąd podczas usuwania kategorii: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
    
    }//GEN-LAST:event_btnUsunKatActionPerformed

    private void btnEdytujKatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEdytujKatActionPerformed
        try {
        // Pobieramy wybraną kategorię z ComboBoxa
        String wybranaKategoria = (String) cmbEdytujKat.getSelectedItem();
        String nowaNazwa = tfEdytuj.getText();  // Pobieramy nową nazwę z TextField

        if (wybranaKategoria == null || nowaNazwa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wybierz kategorię i wprowadź nową nazwę!", "Błąd", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Integer> kategorie = BazaDanych.pobierzKategorie();
        Integer kategoriaId = kategorie.get(wybranaKategoria);

        if (kategoriaId == null) {
            JOptionPane.showMessageDialog(this, "Nie znaleziono ID kategorii!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BazaDanych.edytujKategorie(kategoriaId, nowaNazwa);

        cmbEdytujKat.removeItem(wybranaKategoria);
        cmbEdytujKat.addItem(nowaNazwa);

        JOptionPane.showMessageDialog(this, "Kategoria została zaktualizowana.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
        
        tfEdytuj.setText("");  

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Błąd podczas edytowania kategorii: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
    }   catch (ClassNotFoundException ex) {
            Logger.getLogger(ModyfikujFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnEdytujKatActionPerformed

    private void btnDodajSlowkoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDodajSlowkoActionPerformed
        dodajSlowko();
    }//GEN-LAST:event_btnDodajSlowkoActionPerformed

    private void btnOdswiezActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOdswiezActionPerformed
        zaladujSlowka();
    }//GEN-LAST:event_btnOdswiezActionPerformed

    private void mniPomocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPomocActionPerformed
        JOptionPane.showMessageDialog(ModyfikujFrame.this, 
            "Na początku w 'Załaduj kategorię' wybierz kategorię, w której słówka chcesz edytować, a następnie kliknij przycisk 'Załaduj'.\nPo lewej stronie okienka pojawi się tabela, w której po kliknięciu 'Edytuj' i 'Usuń' w wierszu danego słówka, będziesz mógł je edytować/usunąć.\n Aby dodać nowe słówko, wpisz je w języku polskim i hiszpańskim oraz wybierz kategorię, do której ma należeć.\nJeżeli kategorii, którą chcesz wybrać nie ma na liście, możesz ją stworzyć w 'Dodaj kategorię'. Wpisz jej nazwę, kliknij odpowiedni przycisk i gotowe.\nJeżeli popełniłeś błąd w nazwie, wybierz tę kategorię z menu rozwijanego i wpisz poniżej jej prawidłową nazwę, zatwierdzając przyciskiem.",
            "Pomoc",
            JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_mniPomocActionPerformed
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ModyfikujFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ModyfikujFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ModyfikujFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ModyfikujFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ModyfikujFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDodajKat;
    private javax.swing.JButton btnDodajSlowko;
    private javax.swing.JButton btnEdytujKat;
    private javax.swing.JButton btnOdswiez;
    private javax.swing.JButton btnUsunKat;
    private javax.swing.JButton btnZaladuj;
    private javax.swing.JComboBox<String> cmbEdytujKat;
    private javax.swing.JComboBox<String> cmbKategoriaSlowko;
    private javax.swing.JComboBox<String> cmbUsunKat;
    private javax.swing.JComboBox<String> cmbZaladujKat;
    private javax.swing.JLabel lblDodajKat;
    private javax.swing.JLabel lblEdytujKat;
    private javax.swing.JLabel lblHiszpanski;
    private javax.swing.JLabel lblKategoria;
    private javax.swing.JLabel lblPolski;
    private javax.swing.JLabel lblSlowko;
    private javax.swing.JLabel lblUsunKat;
    private javax.swing.JLabel lblZaladujKat;
    private javax.swing.JMenuBar mbrModyfikuj;
    private javax.swing.JMenuItem mniPomoc;
    private javax.swing.JMenuItem mniStronaGlowna;
    private javax.swing.JMenuItem mniWyjdz;
    private javax.swing.JMenu mnuPomoc;
    private javax.swing.JMenu mnuStronaGlowna;
    private javax.swing.JMenu mnuWyjscie;
    private javax.swing.JPanel pnlDodajKat;
    private javax.swing.JPanel pnlDodajSlowko;
    private javax.swing.JPanel pnlEdytujKat;
    private javax.swing.JPanel pnlOpcje;
    private javax.swing.JPanel pnlTabela;
    private javax.swing.JPanel pnlUsunKat;
    private javax.swing.JPanel pnlZaladuj;
    private javax.swing.JScrollPane scrTabelaScroll;
    private javax.swing.JTable tblTabela;
    private javax.swing.JTextField tfDodajKat;
    private javax.swing.JTextField tfEdytuj;
    private javax.swing.JTextField tfHiszpanski;
    private javax.swing.JTextField tfPolski;
    // End of variables declaration//GEN-END:variables
}
