CREATE TABLE IF NOT EXISTS COMMENTS (
    ID SERIAL PRIMARY KEY
    , P_ID BIGINT DEFAULT NULL
    , BOOK_ID BIGINT DEFAULT NULL
    , USER_ID BIGINT DEFAULT NULL
    , MESSAGE TEXT
    , CREATED TIMESTAMP DEFAULT NOW()
    , FOREIGN KEY(P_ID) REFERENCES COMMENTS(ID)
    , FOREIGN KEY(BOOK_ID) REFERENCES BOOKS(ID)
    , FOREIGN KEY(USER_ID) REFERENCES USERS(ID)
)