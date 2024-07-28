package org.openstreetmap.josm.plugins.movemembership;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.io.importexport.OsmImporter;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.openstreetmap.josm.io.IllegalDataException;

public final class ImportUtils {
    /**
     * Source: <a href="https://github.com/tsmock/KaartValidatorPlugin">KaartValidatorPlugin</a>
     */
    public static DataSet importOsmFile(File file, String layerName) {

        OsmImporter importer = new OsmImporter();
        ProgressMonitor progressMonitor = NullProgressMonitor.INSTANCE;

        try {
            InputStream in = new FileInputStream(file);
            OsmImporter.OsmImporterData oid = importer.loadLayer(in, file, layerName, progressMonitor);
            OsmDataLayer layer = oid.getLayer();
            return layer.getDataSet();

        } catch (FileNotFoundException | IllegalDataException e) {
            e.printStackTrace();
        }

        return null;
    }

}
