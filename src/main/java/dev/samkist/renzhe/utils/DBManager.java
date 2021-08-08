package dev.samkist.renzhe.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.experimental.filters.Filters;
import dev.samkist.renzhe.data.LockdownData;
import dev.samkist.renzhe.data.MuteData;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

public class DBManager {
	private static MongoClient mongoClient;
	private static final CodecRegistry codecRegistry =
			org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
					org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	private static Datastore datastore;


	public static void initialize(String database, String connectionString) {
		mongoClient = MongoClients.create(MongoClientSettings.builder()
				.codecRegistry(codecRegistry)
				.applyConnectionString(new ConnectionString(connectionString))
				.build());


		datastore = Morphia.createDatastore(mongoClient, database);
	}

	protected static void saveMute(MuteData mute) {
		datastore.save(mute);
	}

	protected static void revokeMute(String discordId) {
		datastore.find(MuteData.class)
				.filter(Filters.eq("discordId", discordId))
				.delete();
	}

	protected static Optional<MuteData> getMute(String discordId) {
		final MuteData result = datastore.find(MuteData.class)
				.filter(Filters.eq("discordId", discordId))
				.first();
		return Optional.ofNullable(result);
	}

	protected static HashMap<String, MuteData> loadMutes() {
		final Iterator<MuteData> result = datastore.find(MuteData.class).iterator();
		HashMap<String, MuteData> mutes = new HashMap<>();
		while(result.hasNext()) {
			MuteData next = result.next();
			mutes.put(next.discordId(), next);
		}
		return mutes;
	}

	protected static void saveLockdown(LockdownData lockdown) {
		datastore.save(lockdown);
	}

	protected static void revokeLockdown(String channelId) {
		datastore.find(LockdownData.class)
				.filter(Filters.eq("channelId", channelId))
				.delete();
	}

	protected static HashMap<String, LockdownData> loadLockdowns() {
		final Iterator<LockdownData> result = datastore.find(LockdownData.class).iterator();
		HashMap<String, LockdownData> lockdowns = new HashMap<>();
		while(result.hasNext()) {
			LockdownData next = result.next();
			lockdowns.put(next.channelId(), next);
		}
		return lockdowns;
	}

	protected static Optional<LockdownData> getLockdown(String channelId) {
		final LockdownData result = datastore.find(LockdownData.class)
				.filter(Filters.eq("channelId", channelId))
				.first();
		return Optional.ofNullable(result);
	}
}
