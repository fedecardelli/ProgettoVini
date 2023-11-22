package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * The class {@code Server} defines a server that waits
 * for a message and then sends an answer.
 *
**/

public class Server
{
  private static final int COREPOOL = 5;
  private static final int MAXPOOL = 100;
  private static final long IDLETIME = 5000;
  private static final int SPORT = 4444;

  //DB access variables
  private static final String jdbcUrl = "jdbc:mysql://localhost:3306";
  private static final String username = "fedecardelli";
  private static final String password = "CRDFRC01@g!"
  ;
  private ServerSocket socket;
  private ThreadPoolExecutor pool;
  private LinkedBlockingQueue<Object> messageQueue;


  /**
   * Class constructor.
   *
   * @throws IOException if the creation of the server socket fails.
   *
  **/

  public Server() throws IOException
  {
    this.socket = new ServerSocket(SPORT);
    this.messageQueue = new LinkedBlockingQueue<Object>();
  }

  /**
   * Runs the server code.
   *
  **/
  private void run()
  {
    this.pool = new ThreadPoolExecutor(COREPOOL, MAXPOOL, IDLETIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    System.out.println("Server Running");
    while (true)
    {
      try
      {
        Socket s = this.socket.accept();
        System.out.println("Client Connected");
        this.pool.execute(new ServerThread(this, s));
      }
      catch (Exception e)
      {
        System.out.println("FATAL ERROR: Server died");
        e.printStackTrace();
        break;
      }
    }
    this.pool.shutdown();
  }

  /**
   * Gets the server pool.
   *
   * @return the thread pool.
   *
  **/
  public ThreadPoolExecutor getPool()
  {
    return this.pool;
  }
  /**
   * Gets first object from queue
   * 
   * @return the upper object of queue
   * @throws InterruptedException
   */
  public Object queueTake() throws InterruptedException{
    return this.messageQueue.take();
  }
 /**
   * Puts object in queue
   * 
   * @return nothing
 * @throws InterruptedException
   */
  public void queuePut(Object o) throws InterruptedException{
    this.messageQueue.put(o);
  }
  /**
   * Closes the server execution.
   *
  **/
  public void close()
  {
    try
    {
      this.socket.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally{
      this.pool.shutdown();
    }
  }

  /**
   * Starts the demo.
   *
   * @param args  the method does not requires arguments.
   *
   * @throws IOException if the execution fails.
   *
  **/
  public static void main(final String[] args) throws IOException
  {
    new Server().run();
  }
}
