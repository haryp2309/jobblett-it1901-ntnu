package jobblett.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jobblett.core.JobShiftList;

import java.io.IOException;

public class JobShiftListDeserializer extends StdDeserializer<JobShiftList> {
    protected JobShiftListDeserializer() {
        super(JobShiftList.class);
    }

    public JobShiftList deserialize(JsonNode node) throws IOException {
        ArrayNode arrayNode = (ArrayNode) node.get("jobShifts");
        JobShiftList jobShiftList = new JobShiftList();
        for (JsonNode jobShiftNode : arrayNode) {
            jobShiftList.addJobShift(new JobShiftDeserializer().deserialize(jobShiftNode));
        }
        return jobShiftList;
    }

    @Override
    public JobShiftList deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        TreeNode treeNode = jsonParser.getCodec().readTree(jsonParser);
        return deserialize((JsonNode) treeNode);
    }
}