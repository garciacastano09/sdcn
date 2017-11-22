package es.upm.sdcn.practicaFinal;

public interface Operation <Node, Data>{

    public OperationEnum getOperation();

    public Node getNode();

    public Node[] getNodeTable();

    public Data getData();

    public Data[] getDataMap();

}