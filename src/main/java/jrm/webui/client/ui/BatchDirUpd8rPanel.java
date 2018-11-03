package jrm.webui.client.ui;

import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class BatchDirUpd8rPanel extends VLayout
{
	ListGrid src;
	ListGrid sdr;
	
	public BatchDirUpd8rPanel()
	{
		setHeight100();
		addMember(src = new ListGrid() {{
			setHeight("30%");
			setShowResizeBar(true);
			setCanEdit(false);
			setCanHover(true);
			setHoverAutoFitWidth(true);
			setHoverAutoFitMaxWidth("50%");
			setSelectionType(SelectionStyle.MULTIPLE);
			setCanSort(false);
			setAutoFetchData(true);
			setDataSource(new RestDataSource() {{
				setID("BatchDat2DirSrc");
				setDataURL("/datasources/"+getID());
				setDataFormat(DSDataFormat.XML);
				setOperationBindings(
					new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}},
					new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}}
				);
				setFields(
					new DataSourceTextField("name") {{
						setPrimaryKey(true);
					}}
				);
			}});
		}});
		addMember(sdr = new ListGrid() {
			{
				setHeight("70%");
				setCanEdit(true);
				setCanHover(true);
				setHoverAutoFitWidth(true);
				setHoverAutoFitMaxWidth("50%");
				setSelectionType(SelectionStyle.MULTIPLE);
				setCanSort(false);
				setAutoFitExpandField("result");
				setAutoFitFieldsFillViewport(true);
				setAutoFetchData(true);
				setCanExpandRecords(true);
				setDataSource(new RestDataSource() {{
					setID("BatchDat2DirSDR");
					setDataURL("/datasources/"+getID());
					setDataFormat(DSDataFormat.XML);
					setOperationBindings(
						new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.ADD);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.REMOVE);setDataProtocol(DSProtocol.POSTXML);}},
						new OperationBinding(){{setOperationType(DSOperationType.UPDATE);setDataProtocol(DSProtocol.POSTXML);}}
					);
					setFields(
						new DataSourceTextField("src") {{
							setPrimaryKey(true);
							setCanEdit(false);
						}},
						new DataSourceTextField("dst") {{
							setCanEdit(false);
						}},
						new DataSourceTextField("result") {{
							setCanEdit(false);
						}},
						new DataSourceBooleanField("selected") {{
						}}
					);
				}});
				setFields(
					new ListGridField("src") {{
						setAlign(Alignment.RIGHT);
						setCellFormatter(new CellFormatter()
						{
							@Override
							public String format(Object value, ListGridRecord record, int rowNum, int colNum)
							{
								return "<div style='overflow:hidden;text-overflow:ellipsis;direction:rtl'>"+value+"</div>";
							}
						});
					}},
					new ListGridField("dst") {{
						setAlign(Alignment.RIGHT);
						setCellFormatter(new CellFormatter()
						{
							@Override
							public String format(Object value, ListGridRecord record, int rowNum, int colNum)
							{
								return "<div style='overflow:hidden;text-overflow:ellipsis;direction:rtl'>"+value+"</div>";
							}
						});
					}},
					new ListGridField("result") {{
					}},
					new ListGridField("selected") {{
						setWidth(20);
						setAlign(Alignment.CENTER);
					}}
				);
			}
			
			@Override
			protected Canvas getExpansionComponent(ListGridRecord record)
			{
				return new ListGrid()
				{
					{
						setHeight(200);
						setCanEdit(true);
						setCanHover(true);
						setHoverAutoFitWidth(true);
						setHoverAutoFitMaxWidth("50%");
						setSelectionType(SelectionStyle.NONE);
						setCanSort(false);
						setShowRecordComponents(true);
						setShowRecordComponentsByCell(true);
						setAutoFitExpandField("src");
						setAutoFitFieldsFillViewport(true);
						setDataSource(new RestDataSource() {{
							setID("BatchDat2DirResult");
							setDataURL("/datasources/"+getID());
							setDataFormat(DSDataFormat.XML);
							setOperationBindings(
								new OperationBinding(){{setOperationType(DSOperationType.FETCH);setDataProtocol(DSProtocol.POSTXML);}}
							);
							setFields(
								new DataSourceTextField("src","Dat/XML") {{
									setPrimaryKey(true);
									setForeignKey(sdr.getDataSource().getID()+".src");
									setCanEdit(false);
								}},
								new DataSourceIntegerField("have") {{
									setCanEdit(false);
								}},
								new DataSourceIntegerField("miss") {{
									setCanEdit(false);
								}},
								new DataSourceIntegerField("total") {{
									setCanEdit(false);
								}}
							);
						}});
						setFields(
							new ListGridField("src") {{
								setAlign(Alignment.RIGHT);
								setCellFormatter(new CellFormatter()
								{
									@Override
									public String format(Object value, ListGridRecord record, int rowNum, int colNum)
									{
										return "<div style='overflow:hidden;text-overflow:ellipsis;direction:rtl'>"+value+"</div>";
									}
								});
							}},
							new ListGridField("have") {{
								setWidth(70);
							}},
							new ListGridField("miss") {{
								setWidth(70);
							}},
							new ListGridField("total") {{
								setWidth(70);
							}},
							new ListGridField("report") {{
								setAlign(Alignment.CENTER);
								setDefaultWidth(70);
								setAutoFitWidth(true);
								setCanEdit(false);
							}}
						);
						fetchRelatedData(record, sdr.getDataSource());
					}
					
					@Override
					protected Canvas createRecordComponent(ListGridRecord record, Integer colNum)
					{
						switch(getFieldName(colNum))
						{
							case "report":
							{
								return new IButton("Report") {{
									setAutoFit(true);
								}};
							}
						}
						return super.createRecordComponent(record, colNum);
					}
				};
			}
		});
		addMember(new HLayout() {{
			setHeight(20);
			addMember(new LayoutSpacer("*",20));
			addMember(new DynamicForm() {{
				setColWidths(100,50);
				setWrapItemTitles(false);
				setItems(new CheckboxItem("dry_run", "Dry Run") {{
					setLabelAsTitle(true);
					setShowLabel(false);
				}});
			}});
			addMember(new IButton("Start", event-> {}));
		}});
	}

}
