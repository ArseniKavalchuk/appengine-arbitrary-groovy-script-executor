// Example of Datastore API usage
import com.google.appengine.api.datastore.*

class User {
    String name;
    boolean isCheat; 
}

def ds = DatastoreServiceFactory.getDatastoreService()
def users = [
    new User(name: 'Arseny Kovalchuk', isCheat: true),
    new User(name: 'Mr. Smith', isCheat: false),
    new User(name: 'Mrs. Smith', isCheat: false)
]

for (def user in users) {
    def entity = new Entity('User')
    entity.setProperty('userName', user.name)
    entity.setUnindexedProperty('isCheat', user.isCheat)
    ds.put(entity)
}

def preparedQuery = ds.prepare(new Query('User'))
def entities = preparedQuery.asList(FetchOptions.Builder.withChunkSize(10).limit(10).offset(0))
def userIds = []
entities.each {
    if (it.getProperty('isCheat')) {
        userIds << it.key.id
    }
}
userIds