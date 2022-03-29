package me.dthb.drq.player;

import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.dthb.drq.DevRoomQuests;
import me.dthb.drq.util.exception.DatabaseException;
import me.dthb.drq.util.gson.TableSerializer;
import org.bson.Document;
import org.bson.conversions.Bson;

public class PlayerRepository {

    private final MongoCollection<Document> collection;
    private final MongoClient mongoClient;
    private final Gson gson;

    public PlayerRepository(DevRoomQuests plugin) throws DatabaseException {

        gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Table.class, new TableSerializer())
                .create();

        String mongoUri = plugin.getConfig().getString("mongo-uri");
        verifyString(mongoUri, "Invalid URI");

        String mongoDb = plugin.getConfig().getString("mongo-database");
        verifyString(mongoDb, "Invalid Database");

        String mongoColl = plugin.getConfig().getString("mongo-collection");
        verifyString(mongoColl, "Invalid Collection");

        mongoClient = MongoClients.create(mongoUri);
        MongoDatabase database = mongoClient.getDatabase(mongoDb);
        collection = database.getCollection(mongoColl);
    }

    public PlayerData playerData(String id) {
        Document document = collection.find(Filters.eq(id)).first();

        if (document == null)
            return null;

        String docJson = document.toJson();
        return gson.fromJson(docJson, PlayerData.class);
    }

    public void saveData(PlayerData data) {
        String json = gson.toJson(data);
        Document dataDoc = Document.parse(json);

        Bson filter = Filters.eq(data._id());
        ReplaceOptions options = new ReplaceOptions().upsert(true);
        collection.replaceOne(filter, dataDoc, options);
    }

    public void close() {
        mongoClient.close();
    }

    private void verifyString(String s, String message) throws DatabaseException {
        if (s == null || s.isEmpty())
            throw new DatabaseException(message);
    }

}
