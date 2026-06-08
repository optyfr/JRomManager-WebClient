package jrm.webui.client.datasources;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

public class DSBatchDat2DirSrc extends RestDataSource {
    private static final String BASENAME = "BatchDat2DirSrc";

    private DSBatchDat2DirSrc() {
        setID(BASENAME);
        setDataURL("/datasources/" + BASENAME);
        setDataFormat(DSDataFormat.XML);
        OperationBinding fetchob = new OperationBinding();
        fetchob.setOperationType(DSOperationType.FETCH);
        fetchob.setDataProtocol(DSProtocol.POSTXML);
        OperationBinding addob = new OperationBinding();
        addob.setOperationType(DSOperationType.ADD);
        addob.setDataProtocol(DSProtocol.POSTXML);
        OperationBinding removeob = new OperationBinding();
        removeob.setOperationType(DSOperationType.REMOVE);
        removeob.setDataProtocol(DSProtocol.POSTXML);
        setOperationBindings(fetchob, addob, removeob);
        DataSourceTextField nameField = new DataSourceTextField("name");
        nameField.setPrimaryKey(true);
        setFields(
                nameField);
    }

    public static DSBatchDat2DirSrc getInstance() {
        if (null == get(BASENAME))
            return new DSBatchDat2DirSrc();
        return (DSBatchDat2DirSrc) get(BASENAME);
    }
}
