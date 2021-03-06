import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.json.JSONObject;

@BelongsTo(parent = Node.class, foreignKeyName = "from_id")
public class Edge extends TableModel{

    @Override
    public LazyList<TableModel> read(JSONObject params){

        LazyList<TableModel> modelList = null;
        String arg = null;
        if(params.has("from_id")){
            arg = params.get("from_id").toString();
            if(arg.equals("*")){
                modelList = Edge.findAll();
            }
            else{
                modelList = Edge.find("from_id = ?", Integer.parseInt(params.get("from_id").toString()));
            }
        }

        return modelList;
    }

    @Override
    public LazyList<TableModel> add(JSONObject params){
        LazyList<TableModel> modelList = null;
        if(!params.has("from_id") || !params.has("to_id")){
            return null;
        }
        Edge edge = new Edge();
        edge.set("from_id", Integer.parseInt(params.get("from_id").toString())).set("to_id", Integer.parseInt(params.get("to_id").toString()));

        edge.saveIt();
        long lastUpdatedId = (long) edge.getId();
        modelList = Edge.find("id = ?",lastUpdatedId);
        return modelList;
    }

    @Override
    public LazyList<TableModel> update(JSONObject params){

        LazyList<TableModel> modelList = null;
        Edge edge = new Edge();
        int to_id;

        if(params.has("from_id") && params.has("to_id") && params.has("to_id_new")){
            int from_id = Integer.parseInt(params.get("from_id").toString());
            to_id = Integer.parseInt(params.get("to_id").toString());
            edge = Edge.findFirst("from_id = ? and to_id = ?", from_id, to_id);
        }
        if(edge == null){
            return null;
        }

        to_id = Integer.parseInt(params.get("to_id_new").toString());
        edge.set("to_id", to_id);
        edge.saveIt();
        int lastUpdatedId = (int) edge.getId();
        modelList = Edge.find("id = ?",lastUpdatedId);
        return modelList;
    }

    @Override
    public boolean remove(JSONObject params){
        Edge edge = new Edge();
        if(params.has("id")){
            edge = Edge.findById(Integer.parseInt(params.get("id").toString()));
        } else if(params.has("from_id") && params.has("to_id")){
            int from_id = Integer.parseInt(params.get("from_id").toString());
            int to_id = Integer.parseInt(params.get("to_id").toString());
            edge = Edge.findFirst("from_id = ? and to_id = ?", from_id, to_id);
        }
        if(edge == null){
            return false;
        }
        edge.delete();
        return true;
    }

    public LazyList<TableModel> getEntityAssociations(JSONObject params){
        LazyList<TableModel> associationList = null;

        String arg = null;

        /*TODO
        find better way implement this query*/
        if(params.has("from_id") && params.has("type_id")){
            int from_id = Integer.parseInt(params.get("from_id").toString());
            arg = params.get("type_id").toString();

            if(arg.equals("*")){
                associationList = Edge.find("from_id = ?", from_id);
            }else {
                int type_id = Integer.parseInt(arg);
                associationList = Edge.findBySQL("SELECT edges.from_id, edges.id, edges.to_id, nodes.name FROM edges, nodes where edges.to_id = nodes.id && edges.from_id = " + from_id + " && nodes.type_id = " + type_id + ";");
            }
        }
        return associationList;
    }

}
