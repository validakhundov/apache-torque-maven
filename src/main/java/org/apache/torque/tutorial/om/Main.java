package org.apache.torque.tutorial.om;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.tutorial.om.generated.Author;
import org.apache.torque.tutorial.om.generated.AuthorPeer;
import org.apache.torque.tutorial.om.generated.Book;
import org.apache.torque.tutorial.om.generated.BookPeer;
import org.apache.torque.tutorial.om.generated.Publisher;
import org.apache.torque.tutorial.om.generated.PublisherPeer;

import java.util.List;

public class Main {
    public static void main(String[] args) throws TorqueException {

        // Initializing Logging
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);

        PropertiesConfiguration torqueConfiguration = new PropertiesConfiguration();
        torqueConfiguration.setProperty("torque.database.default", "postgres");
        torqueConfiguration.setProperty("torque.database.postgres.adapter", "postgresql");
        torqueConfiguration.setProperty("torque.dsfactory.postgres.factory", "org.apache.torque.dsfactory.SharedPoolDataSourceFactory");
        torqueConfiguration.setProperty("torque.dsfactory.postgres.connection.driver", "org.postgresql.Driver");
        torqueConfiguration.setProperty("torque.dsfactory.postgres.connection.url", "jdbc:postgresql://127.0.0.1:5432/postgres");
        torqueConfiguration.setProperty("torque.dsfactory.postgres.connection.user", "postgres");
        torqueConfiguration.setProperty("torque.dsfactory.postgres.connection.password", "postgres");

        Torque.init(torqueConfiguration);

        /*
         * Creating new objects. These will be inserted into your database
         * automatically when the save method is called.
         */
        Publisher addison = new Publisher();
        addison.setName("Addison Wesley Professional");
        addison.save();

        Author bloch = new Author();
        bloch.setFirstName("Joshua");
        bloch.setLastName("Bloch");
        bloch.save();

        /*
         * An alternative method to inserting rows in your database.
         */
        Author stevens = new Author();
        stevens.setFirstName("W.");
        stevens.setLastName("Stevens");
        AuthorPeer.doInsert(stevens);

        /*
         * Using the convenience methods to handle the foreign keys.
         */
        Book effective = new Book();
        effective.setTitle("Effective Java");
        effective.setISBN("0-618-12902-2");
        effective.setPublisher(addison);
        effective.setAuthor(bloch);
        effective.save();

        /*
         * Inserting the foreign-keys manually.
         */
        Book tcpip = new Book();
        tcpip.setTitle("TCP/IP Illustrated, Volume 1");
        tcpip.setISBN("0-201-63346-9");
        tcpip.setPublisherId(addison.getPublisherId());
        tcpip.setAuthorId(stevens.getAuthorId());
        tcpip.save();

        /*
         * Selecting all books from the database and printing the results to
         * stdout using our helper method defined in BookPeer (doSelectAll).
         */
        System.out.println("Full booklist:\n");
        List<Book> booklist = BookPeer.doSelectAll();
        printBooklist(booklist);

        /*
         * Selecting specific objects. Just search for objects that match
         * this criteria (and print to stdout).
         */
        System.out.println("Booklist (specific ISBN):\n");
        Criteria crit = new Criteria();
        crit.where(BookPeer.ISBN, "0-201-63346-9");
        booklist = BookPeer.doSelect(crit);
        printBooklist(booklist);

        /*
         * Updating data. These lines will swap the authors of the two
         * books. The booklist is printed to stdout to verify the results.
         */
        effective.setAuthor(stevens);
        effective.save();

        tcpip.setAuthor(bloch);
        BookPeer.doUpdate(tcpip);

        System.out.println("Booklist (authors swapped):\n");
        booklist = BookPeer.doSelectAll();
        printBooklist(booklist);

        /*
         * Deleting data. These lines will delete the data that matches the
         * specified criteria.
         */
        crit = new Criteria();
        crit.where(BookPeer.ISBN, "0-618-12902-2");
        BookPeer.doDelete(crit);

        crit = new Criteria();
        crit.where(BookPeer.ISBN, "0-201-63346-9");
        crit.and(BookPeer.TITLE, "TCP/IP Illustrated, Volume 1");
        BookPeer.doDelete(crit);

        /*
         * Deleting data by passing Data Objects instead of specifying
         * criteria.
         */
        AuthorPeer.doDelete(bloch);
        AuthorPeer.doDelete(stevens);
        PublisherPeer.doDelete(addison);

        System.out.println("Booklist (should be empty):\n");
        booklist = BookPeer.doSelectAll();
        printBooklist(booklist);
    }

    /*
     * Helper method to print a booklist to standard out.
     */
    private static void printBooklist(List<Book> booklist) {
        for (Book book : booklist) {
            System.out.println(book);
        }
    }

}
