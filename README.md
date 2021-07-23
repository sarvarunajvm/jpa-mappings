
# All the things about JPA mapping

## Entity Lifecycle Model in JPA & Hibernate

The entity lifecycle model is one of the  core concepts of JPA and all its implementations. Even though it’s not directly visible when working with JPA’s  _EntityManager_, it affects all operations you perform. The different states of the model define how your persistence provider, e.g. Hibernate, handles your entity objects. This includes if it loads it from the database or gets it from an internal cache, if it persists changes and if it removes the entity.

But before we talk about the different lifecycle states, I need to explain a concept called persistence context.

- 1  JPA’s Persistence Context
- 2  JPA’s 4 Lifecycle States
  - 2.1  Transient
  - 2.2  Managed
  - 2.3  Detached
  - 2.4  Reattaching an entity
  - 2.5  Removed
- 3  Conclusion

## JPA’s Persistence Context

The persistence context is one of the main concepts in JPA. You can think of it as a set of all entity objects that you used in your current use case. Each of them represents a record in the database.

The specification defines it as follows:

> A persistence context is a set of entity instances in which for any persistent entity identity there is a unique entity instance. Within the persistence context, the entity instances and their lifecycle are managed.
> 
> JPA Specification – Chapter 03: Entity Operations  ([URL](https://github.com/eclipse-ee4j/jpa-api/blob/master/spec/src/main/asciidoc/ch03-entity-operations.adoc))

Based on this definition, we can now talk about the lifecycle model.

## JPA’s 4 Lifecycle States

The lifecycle model consists of the 4 states  _transient_,  _managed_,  _removed,_  and  _detached_.

### Transient

The lifecycle state of a newly instantiated entity object is called  _transient_. The entity hasn’t been persisted yet, so it doesn’t represent any database record.

Your persistence context doesn’t know about your newly instantiate object. Because of that, it doesn’t automatically perform an SQL INSERT statement or tracks any changes. As long as your entity object is in the lifecycle state  _transient_, you can think of it as a basic Java object without any connection to the database and any JPA-specific functionality.

    Author author = new Author();
    author.setFirstName("Saravanan");
    author.setLastName("Kalimuthu");

That changes when you provide it to the  _EntityManager.find_  method. The entity object then changes its lifecycle state to  _managed_ and gets attached to the current persistence context.

### Managed

All entity objects attached to the current persistence context are in the lifecycle state  _managed_. That means that your persistence provider, e.g. Hibernate, will detect any changes on the objects and generate the required SQL INSERT or UPDATE statements when it flushes the persistence context.

There are different ways to get an entity to the lifecycle state  _managed_:

1. You can call the  _EntityManager.persist_  method with a new entity object.

    Author author = new Author();
    author.setFirstName("Saravanan");
    author.setLastName("Kalimuthu");
    em.persist(author);

2. You can load an entity object from the database using the  _EntityManager.find_ method, a  JPQL query, a  _CriteriaQuery_, or a native SQL query.

    Author author = em.find(Author.class, 1L);

3. You can merge a detached entity by calling the  _EntityManager.merge_ method or update it by calling the  _update_ method on your Hibernate  _Session_.

    em.merge(author);

### Detached

An entity that was previously managed but is no longer attached to the current persistence context is in the lifecycle state  _detached_.

An entity gets detached when you close the persistence context. That typically happens after a request got processed. Then the database transaction gets committed, the persistence context gets closed, and the entity object gets returned to the caller. The caller then retrieves an entity object in the lifecycle state  _detached_.

You can also programmatically detach an entity by calling the  _detach_  method on the  _EntityManager_.

    em.detach(author);

There are only very few  performance tuning reasons to detach a managed entity. If you decide to detach an entity, you should first flush the persistence context to avoid losing any pending changes.

### Reattaching an entity

You can reattach an entity by calling the _update_ method on your Hibernate _Session_ or the _merge_ method on the _EntityManager_. There are a few subtle differences between these operations.

In both cases, the entity changes its lifecycle state to managed.

### Removed

When you call the remove method on your  _EntityManager_, the mapped database record doesn’t get removed immediately. The entity object only changes its lifecycle state to  _removed_.

During the next flush operation, Hibernate will generate an SQL DELETE statement to remove the record from the database table.

    em.remove(author);

## Conclusion

All entity operations are based on JPA’s lifecycle model. It consists of 4 states, which define how your persistence provider handles the entity object.

New entities that are not attached to the current persistence context are in the  _transient_ state.

If you call the persist method on the EntityManager with a new entity object or read an existing record from the database, the entity object is in the managed state. It’s connected to the current persistence context. Your persistence context will generate the required SQL INSERT and UPDATE statement to persist the current state of the object.

Entities in the state removed are scheduled for removal. The persistence provider will generate and execute the required SQL DELETE statement during the next flush operation.

If a previously managed entity is no longer associated with an active persistence context, it has the lifecycle state detached. Changes to such an entity object will not be persisted in the database.

----

# Map Associations with JPA

Association mappings are one of the key features of JPA and  [Hibernate](http://www.hibernate.org/). They model the relationship between two database tables as attributes in your domain model. That allows you to easily navigate the associations in your domain model and JPQL or Criteria queries.

JPA and Hibernate support the same associations as you know from your relational database model. You can use:

- one-to-one
- many-to-one
- many-to-many

You can map each of them as a uni- or bidirectional association. That means you can either model them as an attribute on only one of the associated entities or on both. That has no impact on your database mapping, but it defines in which direction you can use the relationship in your domain model and JPQL or Criteria queries. I will explain that in more detail in the first example.

- 1  Many-to-One Associations
  - 1.1  Unidirectional Many-to-One Association
  - 1.2  Unidirectional One-to-Many Association
  - 1.3  Bidirectional Many-to-One Associations
- 2  Many-to-Many Associations
  - 2.1  Unidirectional Many-to-Many Associations
  - 2.2  Bidirectional Many-to-Many Associations
- 3  One-to-One Associations
  - 3.1  Unidirectional One-to-One Associations
  - 3.2  Bidirectional One-to-One Associations
- 4  Summary

## Many-to-One Associations

An order consists of multiple items, but each item belongs to only one order. That is a typical example for a many-to-one association. If you want to model this in your database model, you need to store the  primary key  of the  _Order_  record as a foreign key in the  _OrderItem_  table.

With JPA and Hibernate, you can model this in 3 different ways. You can either model it as a bidirectional association with an attribute on the  _Order_  and the  _OrderItem_  entity. Or you can model it as a unidirectional relationship with an attribute on the  _Order_  or  the  _OrderItem_  entity.

### Unidirectional Many-to-One Association

Let’s take a look at the unidirectional mapping on the  _OrderItem_  entity first. The  _OrderItem_  entity represents the many side of the relationship and the  _OrderItem_  table contains the foreign key of the record in the  _Order_  table.

As you can see in the following code snippet, you can model this association with an attribute of type  _Order_  and a  _@ManyToOne_  annotation. The  _Order order_  attribute models the association, and the annotation tells Hibernate how to map it to the database.

    @Entity
    public class OrderItem {
    @ManyToOne
    private Order order;
    ...
    }

That is all you need to do to model this association. By default, Hibernate generates the name of the foreign key column based on the name of the relationship mapping attribute and the name of the primary key attribute. In this example, Hibernate would use a column with the name  _order_id to store the foreign key to the  _Order_  entity.

If you want to use a different column, you need to define the foreign key column name with a  _@JoinColumn_  annotation. The example in the following code snippet tells Hibernate to use the column  _fk_order_  to store the foreign key.

    @Entity
    public class OrderItem {
    @ManyToOne
    @JoinColumn(name = “fk_order”)
    private Order order;
    …
    }

You can now use this association in your business code to get the  _Order_  for a given  _OrderItem_  and to add or remove an  _OrderItem_  to or from an existing  _Order_.

    Order o = em.find(Order.class, 1L);
    OrderItem i = new OrderItem();
    i.setOrder(o);
    em.persist(i);

That’s all about the mapping of unidirectional many-to-one associations for now. If you want to dive deeper, you should take a look at  _FetchTypes_.

But now, let’s continue with the association mappings and talk about unidirectional one-to-many relationships next. As you might expect, the mapping is very similar to this one.  

### Unidirectional One-to-Many Association

The unidirectional one-to-many relationship mapping is not very common. In the example of this post, it only models the association on the  _Order_  entity and not on the  _OrderItem_.

The basic mapping definition is very similar to the many-to-one association. It consist of the  _List items_  attribute which stores the associated entities and a  _@OneToMany_  association.

    @Entity
    public class Order {
    @OneToMany
    private List<OrderItem> items = new ArrayList<OrderItem>();
    …
    }

But this is most likely not the mapping you’re looking for because Hibernate uses an association table to map the relationship. If you want to avoid that, you need to use a  _@JoinColumn_  annotation to specify the foreign key column.

The following code snippet shows an example of such a mapping. The  _@JoinColumn_  annotation tells Hibernate to use the  _fk_order column in the _OrderItem_ table to join the two database tables.

    @Entity
    public class Order {
    @OneToMany
    @JoinColumn(name = “fk_order”)
    private List<OrderItem> items = new ArrayList<OrderItem>();
    …
    }

You can now use the association in your business code to get all  _OrderItem_s of an  _Order_  and to add or remove an  _OrderItem_  to or from an  _Order_.

    Order o = em.find(Order.class, 1L);
    OrderItem i = new OrderItem();
    o.getItems().add(i);
    em.persist(i);

### Bidirectional Many-to-One Associations

The bidirectional Many-to-One association mapping is the most common way to model this relationship with JPA and Hibernate. It uses an attribute on the  _Order_  and the  _OrderItem_  entity. This allows you to navigate the association in both directions in your domain model and your JPQL queries.

The mapping definition consists of 2 parts:

- the to-many side of the association which owns the relationship mapping and
- the to-one side which just references the mapping

Let’s take a look at the owning side first. You already know this mapping from the  unidirectional Many-to-One  association mapping. It consists of the  _Order order_  attribute, a  _@ManyToOne_  annotation and an optional  _@JoinColumn_  annotation.

    @Entity
    public class OrderItem {
    @ManyToOne
    @JoinColumn(name = “fk_order”)
    private Order order;
    …
    }

The owning part of the association mapping already provides all the information Hibernate needs to map it to the database. That makes the definition of the referencing part simple. You just need to reference the owning association mapping. You can do that by providing the name of the association-mapping attribute to the  _mappedBy_  attribute of the  _@OneToMany_  annotation. In this example, that’s the  _order_  attribute of the  _OrderItem_  entity.

    @Entity
    public class Order {
    @OneToMany(mappedBy = “order”)
    private List<OrderItem> items = new ArrayList<OrderItem>();
    …
    }

You can now use this association in a similar way as the unidirectional relationships I showed you before. But adding and removing an entity from the relationship requires an additional step. You need to update both sides of the association.

    Order o = em.find(Order.class, 1L);
    OrderItem i = new OrderItem();
    i.setOrder(o);
    o.getItems().add(i);
    em.persist(i);

That is an error-prone task, and a lot of developers prefer to implement it in a utility method which updates both entities.

    @Entity
    public class Order {
    …
    public void addItem(OrderItem item) {
    this.items.add(item);
    item.setOrder(this);
    }
    …
    }

That’s all about many-to-one association mapping for now. You should also take a look at  _FetchTypes_  and how they impact the way Hibernate loads entities from the database.

## Many-to-Many Associations

Many-to-Many relationships are another often used association type. On the database level, it requires an additional association table which contains the  primary key  pairs of the associated entities. But as you will see, you don’t need to map this table to an entity.

A typical example for such a many-to-many association are  _Product_s and  _Store_s. Each  _Store_  sells multiple  _Product_s and each  _Product_  gets sold in multiple  _Store_s.

Similar to the many-to-one association, you can model a many-to-many relationship as a uni- or bidirectional relationship between two entities.

But there is an important difference that might not be obvious when you look at the following code snippets. When you map a many-to-many association, you should use a  _Set_  instead of a  _List_  as the attribute type. Otherwise, Hibernate will take a very inefficient approach to remove entities from the association. It will remove all records from the association table and re-insert the remaining ones. You can avoid that by using a  _Set_  instead of a  _List_  as the attribute type.

OK let’s take a look at the unidirectional mapping first.

### Unidirectional Many-to-Many Associations

Similar to the previously discussed mappings, the unidirectional many-to-many relationship mapping requires an entity attribute and a  _@ManyToMany_  annotation. The attribute models the association and you can use it to navigate it in your domain model or  JPQL queries. The annotation tells Hibernate to map a many-to-many association.

Let’s take a look at the relationship mapping between a  _Store_  and a  _Product_. The  _Set products_  attribute models the association in the domain model and the  _@ManyToMany_  association tells Hibernate to map it as a many-to-many association.

And as I already explained, please note the difference to the previous many-to-one mappings. You should map the associated entities to a  _Set_  instead of a  _List._

    @Entity
    public class Store {
    @ManyToMany
    private Set<Product> products = new HashSet<Product>();
    …
    }

If you don’t provide any additional information, Hibernate uses its default mapping which expects an association table with the name of both entities and the primary key attributes of both entities. In this case, Hibernate uses the _Store_Product_table_ with the columns _store_id_and_product_id_.

You can customize that with a  _@JoinTable_  annotation and its attributes  _joinColumns_  and  _inverseJoinColumns_. The  _joinColumns_  attribute defines the foreign key columns for the entity on which you define the association mapping. The  _inverseJoinColumns_  attribute specifies the foreign key columns of the associated entity.

The following code snippet shows a mapping that tells Hibernate to use the  _store_product_table with the_fk_product_column as the foreign key to the_Product_table and the_fk_store_column as the foreign key to the_Store_  table.

    @Entity
    public class Store {
    @ManyToMany
    @JoinTable(name = “store_product”,
    joinColumns = { @JoinColumn(name = “fk_store”) },
    inverseJoinColumns = { @JoinColumn(name = “fk_product”) })
    private Set<Product> products = new HashSet<Product>();
    …
    }

That’s all you have to do to define an unidirectional many-to-many association between two entities. You can now use it to get a  _Set_  of associated entities in your domain model or to join the mapped tables in a JPQL query.

    Store s = em.find(Store.class, 1L);
    Product p = new Product();
    s.getProducts().add(p);
    em.persist(p);

### Bidirectional Many-to-Many Associations

The bidirectional relationship mapping allows you to navigate the association in both directions. And after you’ve read the post this far, you’re probably not surprised when I tell you that the mapping follows the same concept as the bidirectional mapping of a many-to-one relationship.

One of the two entities owns the association and provides all mapping information. The other entity just references the association mapping so that Hibernate knows where it can get the required information.

Let’s start with the entity that owns the relationship. The mapping is identical to the  unidirectional many-to-many association mapping. You need an attribute that maps the association in your domain model and a  _@ManyToMany_  association. If you want to adapt the default mapping, you can do that with a  _@JoinColumn_  annotation.

    @Entity
    public class Store {
    @ManyToMany
    @JoinTable(name = “store_product”,
    joinColumns = { @JoinColumn(name = “fk_store”) },
    inverseJoinColumns = { @JoinColumn(name = “fk_product”) })
    private Set<Product> products = new HashSet<Product>();
    …
    }

The mapping for the referencing side of the relationship is a lot easier. Similar to the bidirectional many-to-one relationship mapping, you just need to reference the attribute that owns the association.

You can see an example of such a mapping in the following code snippet. The  _Set<<Product>> products_  attribute of the  _Store_  entity owns the association. So, you only need to provide the  _String_  “_products_” to the  _mappedBy_  attribute of the  _@ManyToMany_  annotation.

    @Entity
    public class Product{
    @ManyToMany(mappedBy=”products”)
    private Set<Store> stores = new HashSet<Store>();
    …
    }

That’s all you need to do to define a bidirectional many-to-many association between two entities. But there is another thing you should do to make it easier to use the bidirectional relationship.

You need to update both ends of a bidirectional association when you want to add or remove an entity. Doing that in your business code is verbose and error-prone. It’s, therefore, a good practice to provide helper methods which update the associated entities.

    @Entity
    public class Store {
    public void addProduct(Product p) {
    this.products.add(p);
    p.getStores().add(this);
    }
    public void removeProduct(Product p) {
    this.products.remove(p);
    p.getStores().remove(this);
    }
    …
    }

OK, now we’re done with the definition of the many-to-many association mappings. Let’s take a look at the third and final kind of association: The one-to-one relationship.

## One-to-One Associations

One-to-one relationships are rarely used in relational table models. You, therefore, won’t need this mapping too often. But you will run into it from time to time. So it’s good to know that you can map it in a similar way as all the other associations.

An example for a one-to-one association could be a  _Customer_  and the  _ShippingAddress_. Each  _Customer_  has exactly one  _ShippingAddress_  and each  _ShippingAddress_  belongs to one  _Customer_. On the database level, this mapped by a foreign key column either on the  _ShippingAddress_  or the  _Customer_  table.

Let’s take a look at the unidirectional mapping first.

### Unidirectional One-to-One Associations

As in the previous unidirectional mapping, you only need to model it for the entity for which you want to navigate the relationship in your query or domain model. Let’s say you want to get from the  _Customer_  to the  _ShippingAddress_  entity.

The required mapping is similar to the previously discussed mappings. You need an entity attribute that represents the association, and you have to annotate it with an  _@OneToOne_  annotation.

When you do that, Hibernate uses the name of the associated entity and the name of its primary key attribute to generate the name of the foreign key column. In this example, it would use the column  _shippingaddress_id_. You can customize the name of the foreign key column with a_@JoinColumn_  annotation. The following code snippet shows an example of such a mapping.

    @Entity
    public class Customer{
    @OneToOne
    @JoinColumn(name = “fk_shippingaddress”)
    private ShippingAddress shippingAddress;
    …
    }

That’s all you need to do to define a one-to-one association mapping. You can now use it in your business to add or remove an association, to navigate it in your domain model or to join it in a JPQL query.

    Customer c = em.find(Customer.class, 1L);
    ShippingAddress sa = c.getShippingAddress();

### Bidirectional One-to-One Associations

The bidirectional one-to-one relationship mapping extends the unidirectional mapping so that you can also navigate it in the other direction. In this example, you also model it on the  _ShippingAddress_  entity so that you can get the  _Customer_  for a giving  _ShippingAddress_.

Similar to the previously discussed bidirectional mappings, the bidirectional one-to-one relationship consists of an owning and a referencing side. The owning side of the association defines the mapping, and the referencing one just links to that mapping.

The definition of the owning side of the mapping is identical to the unidirectional mapping. It consists of an attribute that models the relationship and is annotated with a  _@OneToOne_  annotation and an optional  _@JoinColumn_  annotation.

    @Entity
    public class Customer{
    @OneToOne
    @JoinColumn(name = “fk_shippingaddress”)
    private ShippingAddress shippingAddress;
    …
    }

The referencing side of the association just links to the attribute that owns the relationship. Hibernate gets all information from the referenced mapping, and you don’t need to provide any additional information. You can define that with the  _mappedBy_  attribute of the  _@OneToOne_  annotation. The following code snippet shows an example of such a mapping.

    @Entity
    public class ShippingAddress{
    @OneToOne(mappedBy = “shippingAddress”)
    private Customer customer;
    …
    }

## Summary

The relational table model uses many-to-many, many-to-one and one-to-one associations to model the relationship between database records. You can map the same relationships with JPA and Hibernate, and you can do that in an unidirectional or bidirectional way.

The unidirectional mapping defines the relationship only on 1 of the 2 associated entities, and you can only navigate it in that direction. The bidirectional mapping models the relationship for both entities so that you can navigate it in both directions.

The concept for the mapping of all 3 kinds of relationships is the same.

If you want to create an unidirectional mapping, you need an entity attribute that models the association and that is annotated with a  _@ManyToMany_,  _@ManyToOne_,  _@OneToMany_  or  _@OneToOne_  annotation. Hibernate will generate the name of the required foreign key columns and tables based on the name of the entities and their primary key attributes.

The bidirectional associations consist of an owning and a referencing side. The owning side of the association is identical to the unidirectional mapping and defines the mapping. The referencing side only links to the attribute that owns the association.
