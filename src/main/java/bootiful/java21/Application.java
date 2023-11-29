package bootiful.java21;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .headless(false)
                .sources(Application.class)
                .run(args);
    }


    @Bean
    ApplicationRunner swingRunner(CatFactsClient cfc) {
        return args -> SwingUtilities.invokeLater(() -> {

            var catFacts = cfc.facts().data();

            var table = new JTable(new CatFactTableModel(catFacts));
            table.setDefaultRenderer(Object.class, new PaddedTableCellRenderer());
            var font = new Font("Helvetica", Font.PLAIN, 18);
            table.getColumnModel().getColumn(0).setPreferredWidth(200);
            table.getColumnModel().getColumn(1).setPreferredWidth(1000);
            table.setFont(font);
            table.getTableHeader().setFont(font);
            table.setRowHeight((int) (table.getRowHeight() * 2));
            table.setRowMargin((int) (table.getRowHeight() * .3));

            var frame = new JFrame("Cat Facts Table");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new JScrollPane(table), BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.requestFactory(new JdkClientHttpRequestFactory()).build();
    }

    @Bean
    CatFactsClient catFactsClient(RestClient restClient) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build().createClient(CatFactsClient.class);
    }

}

class PaddedTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        var c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (c instanceof JLabel label)
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return c;
    }
}


record CatFact(String fact, long length) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record CatFacts(List<CatFact> data) {
}

interface CatFactsClient {

    @GetExchange("https://catfact.ninja/facts")
    CatFacts facts();

}


class CatFactTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Length", "Fact"};

    private final List<CatFact> catFacts;

    CatFactTableModel(List<CatFact> catFacts) {
        this.catFacts = catFacts;
    }

    @Override
    public int getRowCount() {
        return catFacts.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var catFact = catFacts.get(rowIndex);
        return switch (columnIndex) {
            case 1 -> catFact.fact();
            case 0 -> catFact.length();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        var catFact = catFacts.get(rowIndex);
        var fact = catFact.fact();
        var length = catFact.length();
        switch (columnIndex) {
            case 1:
                fact = ((String) aValue);
                break;
            case 0:
                length = (Long) aValue;
                break;
        }
        catFacts.set(rowIndex, new CatFact(fact, length));

        fireTableCellUpdated(rowIndex, columnIndex);
    }

}