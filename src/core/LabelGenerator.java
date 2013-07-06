package core;


import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

/**
 *
 * @author Alexander
 */
public class LabelGenerator extends StandardCategoryItemLabelGenerator {


    public LabelGenerator() {
    }

    @Override
    public String generateLabel(CategoryDataset dataset, int row, int column) {
        return MatrixHolder.datasetNames[row][column];
    }
}
