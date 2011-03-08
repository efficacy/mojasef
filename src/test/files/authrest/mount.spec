import.packages += org.stringtree.mojasef.rest
import.packages += stubs
users#: 12345=1111
personality$=Items src/test/files/authrest/items.spec
items$=RESTCollection personality

public/          items
# path           class      realm   id-key 
private/         Authorized private OBJECT users items
id/{id}/details  Authorized id id users IdRESTCollection personality
