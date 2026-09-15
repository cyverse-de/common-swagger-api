(ns common-swagger-api.malli.data.tickets-test
  (:require
   [clojure.test :refer [deftest is]]
   [common-swagger-api.malli.data.tickets :as tickets]
   [common-swagger-api.malli.test-util :refer [invalid json-schema-ok valid]]
   [malli.json-schema :as js]))

(def ticket
  {:path              "/iplant/home/janedoe/file.txt"
   :ticket-id         "4ea5ba1f-1a5a-4f2b-a1bc-0f4a0e0c6ac5"
   :download-url      "https://example.org/dl/d/4ea5ba1f-1a5a-4f2b-a1bc-0f4a0e0c6ac5/file.txt"
   :download-page-url "https://example.org/d/4ea5ba1f-1a5a-4f2b-a1bc-0f4a0e0c6ac5"})

(def tickets-by-path {(keyword (:path ticket)) [ticket]})

(def doc-path (keyword "/path/from/request/to/a/file/or/folder"))

(deftest AddTicketQueryParams
  (valid tickets/AddTicketQueryParams
         {:public false}
         {:public true :mode :write :uses-limit 5 :file-write-limit 10 :for-job false})
  (invalid tickets/AddTicketQueryParams
           {}
           {:public false :mode :append}
           {:public false :uses-limit "5"}
           {:public false :extra 1}))

(deftest DeleteTicketQueryParams
  (valid tickets/DeleteTicketQueryParams {} {:for-job true})
  (invalid tickets/DeleteTicketQueryParams {:for-job "true"} {:extra 1}))

(deftest TicketDefinition
  (valid tickets/TicketDefinition ticket)
  (invalid tickets/TicketDefinition
           {}
           (dissoc ticket :ticket-id)
           (assoc ticket :path "")
           (assoc ticket :extra 1)))

(deftest AddTicketResponse
  (valid tickets/AddTicketResponse {:user "ipctest" :tickets []} {:user "ipctest" :tickets [ticket]})
  (invalid tickets/AddTicketResponse
           {:user "ipctest"}
           {:user "ipctest" :tickets ticket}
           {:user "ipctest" :tickets [(dissoc ticket :path)]}
           {:user "ipctest" :tickets [ticket] :extra 1}))

(deftest ListTicketsResponseMap
  (valid tickets/ListTicketsResponseMap {} tickets-by-path)
  (invalid tickets/ListTicketsResponseMap
           {(:path ticket) [ticket]}
           {(keyword (:path ticket)) ticket}))

(deftest ListTicketsResponse
  (valid tickets/ListTicketsResponse {:tickets {}} {:tickets tickets-by-path})
  (invalid tickets/ListTicketsResponse
           {}
           {:tickets [ticket]}
           {:tickets tickets-by-path :extra 1}))

(deftest ListTicketsPathsMap
  (valid tickets/ListTicketsPathsMap {doc-path [ticket]})
  (invalid tickets/ListTicketsPathsMap {} {doc-path ticket} {doc-path [ticket] :extra 1}))

(deftest ListTicketsDocumentation
  (valid tickets/ListTicketsDocumentation {:tickets [{doc-path [ticket]}]})
  (invalid tickets/ListTicketsDocumentation
           {}
           {:tickets {doc-path [ticket]}}
           {:tickets [{doc-path [ticket]}] :extra 1}))

(deftest Tickets
  (valid tickets/Tickets {:tickets []} {:tickets [(:ticket-id ticket)]})
  (invalid tickets/Tickets {} {:tickets [""]} {:tickets [(:ticket-id ticket)] :extra 1}))

(deftest DeleteTicketsResponse
  (valid tickets/DeleteTicketsResponse {:user "ipctest" :tickets [(:ticket-id ticket)]})
  (invalid tickets/DeleteTicketsResponse
           {:tickets [(:ticket-id ticket)]}
           {:user "ipctest"}
           {:user "" :tickets []}
           {:user "ipctest" :tickets [] :extra 1}))

(deftest TicketCommonErrorCodes
  (is (= ["ERR_UNCHECKED_EXCEPTION" "ERR_SCHEMA_VALIDATION" "ERR_TOO_MANY_RESULTS" "ERR_DOES_NOT_EXIST"
          "ERR_NOT_A_USER"]
         tickets/TicketCommonErrorCodes)))

(deftest AddTicketErrorResponses
  (valid tickets/AddTicketErrorResponses
         {:error_code "ERR_NOT_WRITEABLE"}
         {:error_code "ERR_DOES_NOT_EXIST" :reason "Does not exist"}
         {:error_code "ERR_UNCHECKED_EXCEPTION"})
  (invalid tickets/AddTicketErrorResponses {} {:error_code "ERR_NOT_READABLE"} {:error_code "ERR_NOT_A_USER" :x 1}))

(deftest AddTicketResponses
  (is (= #{200 500 :default} (set (keys tickets/AddTicketResponses))))
  (is (= tickets/AddTicketErrorResponses (get-in tickets/AddTicketResponses [500 :body])))
  (valid (get-in tickets/AddTicketResponses [200 :body]) {:user "ipctest" :tickets [ticket]})
  (invalid (get-in tickets/AddTicketResponses [200 :body]) {}))

(deftest ListTicketErrorResponses
  (valid tickets/ListTicketErrorResponses
         {:error_code "ERR_NOT_READABLE"}
         {:error_code "ERR_TOO_MANY_RESULTS" :reason "Too many results"})
  (invalid tickets/ListTicketErrorResponses {} {:error_code "ERR_NOT_WRITEABLE"} {:error_code "ERR_NOT_A_USER" :x 1}))

(deftest ListTicketResponses
  (is (= #{200 500 :default} (set (keys tickets/ListTicketResponses))))
  (is (= tickets/ListTicketErrorResponses (get-in tickets/ListTicketResponses [500 :body])))
  (valid (get-in tickets/ListTicketResponses [200 :body]) {:tickets tickets-by-path})
  (invalid (get-in tickets/ListTicketResponses [200 :body]) {} {:tickets [ticket]})
  (is (= #{:tickets} (set (keys (:properties (js/transform (get-in tickets/ListTicketResponses [200 :body]))))))))

(deftest DeleteTicketErrorResponses
  (valid tickets/DeleteTicketErrorResponses
         {:error_code "ERR_TICKET_DOES_NOT_EXIST"}
         {:error_code "ERR_NOT_WRITEABLE" :reason "Not writeable"})
  (invalid tickets/DeleteTicketErrorResponses
           {}
           {:error_code "ERR_DOES_NOT_EXIST"}
           {:error_code "ERR_NOT_A_USER" :x 1}))

(deftest DeleteTicketResponses
  (is (= #{200 500 :default} (set (keys tickets/DeleteTicketResponses))))
  (is (= tickets/DeleteTicketErrorResponses (get-in tickets/DeleteTicketResponses [500 :body])))
  (valid (get-in tickets/DeleteTicketResponses [200 :body]) {:user "ipctest" :tickets []})
  (invalid (get-in tickets/DeleteTicketResponses [200 :body]) {:tickets []}))

(deftest json-schema
  (json-schema-ok 'common-swagger-api.malli.data.tickets))
