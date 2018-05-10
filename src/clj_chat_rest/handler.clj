(ns clj-chat-rest.handler
      (:import com.mchange.v2.c3p0.ComboPooledDataSource)
      (:use compojure.core)
      (:use ring.util.response)
      (:use cheshire.core)
      (:require [compojure.handler :as handler]
                [compojure.route :as route]
                [clojure.java.jdbc :as jdbc]
                [ring.middleware.json :as middleware]))


;; ============ DB configuration ============

    (def db-config
      {:classname "org.h2.Driver"
       :subprotocol "h2"
       :subname "mem:messages"
       :user ""
       :password ""})

    (defn pool
      [config]
      (let [cpds (doto (ComboPooledDataSource.)
                   (.setDriverClass (:classname config))
                   (.setJdbcUrl (str "jdbc:" (:subprotocol config) ":" (:subname config)))
                   (.setUser (:user config))
                   (.setPassword (:password config))
                   (.setMaxPoolSize 1)
                   (.setMinPoolSize 1)
                   (.setInitialPoolSize 1))]
        {:datasource cpds}))

    (def pooled-db (delay (pool db-config)))

    (defn db-connection [] @pooled-db)


;; ============ DB schema ============
    
    (jdbc/with-connection (db-connection)
      (jdbc/create-table :messages 
                        [:id "bigint primary key auto_increment"]
                        [:sender "varchar(100) not null"]
                        [:message "varchar not null"]
                        [:room "varchar not null"]))


;; ============ DB queries ============    
    
    (defn get-all-messages []
      (response
        (jdbc/with-connection (db-connection)
          (jdbc/with-query-results results
            ["select * from messages"]
            (into [] results)))))
   
    (defn get-all-messages-of-room [room]
      (response
        (jdbc/with-connection (db-connection)
          (jdbc/with-query-results results
            ["select * from messages where room = ?" room]
            (into [] results)))))

    (defn get-message [id]
      (jdbc/with-connection (db-connection)
        (jdbc/with-query-results results
          ["select * from messages where id = ?" id]
          (cond
            (empty? results) {:status 404}
            :else (response (first results))))))

    (defn create-message [message]
        (jdbc/with-connection (db-connection)
            (jdbc/insert-record :messages message))
        {:status 201})


;; ============ REST request handling ============      
    
    (defroutes my-routes
      (GET "/" [] (resource-response "index.html" {:root "public"}))
      (context "/messages" [] (defroutes message-routes
        (GET  "/" [] (get-all-messages))
        (GET  "/:room" [room] (get-all-messages-of-room room))
        (POST "/" {body :body} (create-message body))))
      (route/not-found "Not Found"))

    (def app
        (-> (handler/api my-routes)
            (middleware/wrap-json-body)
            (middleware/wrap-json-response)))

