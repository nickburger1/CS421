import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.util.*;

public class Main {
    // Driver Found at
    // https://github.com/xerial/sqlite-jdbc/releases

    // Compile using "javac Main.java"
    // Execute using "java -classpath ".;sqlite-jdbc-3.39.3.0.jar" Main"

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection con;
        con  = DriverManager.getConnection("jdbc:sqlite:ScheduleMaker.db");
        return con;
    }

    //Build the database and tables
    private static void buildDatabase(boolean DBExists, String filePath) throws ClassNotFoundException, SQLException, IOException, ParseException {
        Connection con;
        Statement state, state2;
        ResultSet res;
        PreparedStatement prep;

        if(!DBExists){
            DBExists = true;
            con = getConnection();
            state = con.createStatement();
            res = state.executeQuery("SELECT * FROM sqlite_master WHERE type = 'table' AND name='SCHEDULE_TABLE'");

            if(!res.next()){
                System.out.println("Building Database...");
                state2 = con.createStatement();

                state2.executeUpdate("CREATE TABLE \"CLASSROOM_TABLE\" (\n" +
                        "\t\"TUID\"\tINTEGER NOT NULL UNIQUE,\n" +
                        "\t\"CLASSROOM_NAME\"\tTEXT NOT NULL UNIQUE,\n" +
                        "\t\"CAPACITY\"\tINTEGER NOT NULL,\n" +
                        "\tPRIMARY KEY(\"TUID\" AUTOINCREMENT)\n" +
                        ");");
                state2.executeUpdate("CREATE TABLE \"COURSES_TABLE\" (\n" +
                        "\t\"TUID\"\tINTEGER NOT NULL UNIQUE,\n" +
                        "\t\"COURSE_ID\"\tTEXT NOT NULL,\n" +
                        "\t\"COURSE_TITLE\"\tTEXT NOT NULL,\n" +
                        "\t\"CREDIT_HOURS\"\tINTEGER NOT NULL,\n" +
                        "\tPRIMARY KEY(\"TUID\" AUTOINCREMENT)\n" +
                        ");");
                state2.executeUpdate("CREATE TABLE \"PROFESSOR_TABLE\" (\n" +
                        "\t\"TUID\"\tINTEGER NOT NULL UNIQUE,\n" +
                        "\t\"PROFESSOR_NAME\"\tTEXT NOT NULL,\n" +
                        "\tPRIMARY KEY(\"TUID\" AUTOINCREMENT)\n" +
                        ");");
                state2.executeUpdate("CREATE TABLE \"SCHEDULE_TABLE\" (\n" +
                        "\t\"TUID\"\tINTEGER NOT NULL UNIQUE,\n" +
                        "\t\"COURSE_TUID\"\tINTEGER NOT NULL,\n" +
                        "\t\"SECTION\"\tINTEGER NOT NULL,\n" +
                        "\t\"CLASSROOM_TUID\"\tINTEGER NOT NULL,\n" +
                        "\t\"PROFESSOR_TUID\"\tINTEGER NOT NULL,\n" +
                        "\t\"START_TIME\"\tTEXT NOT NULL,\n" +
                        "\t\"END_TIME\"\tTEXT NOT NULL,\n" +
                        "\t\"DAYS\"\tTEXT NOT NULL,\n" +
                        "\tFOREIGN KEY(\"CLASSROOM_TUID\") REFERENCES CLASSROOM_TABLE,\n" +
                        "\tFOREIGN KEY(\"PROFESSOR_TUID\") REFERENCES PROFESSOR_TABLE,\n" +
                        "\tFOREIGN KEY(\"COURSE_TUID\") REFERENCES COURSES_TABLE,\n" +
                        "\tPRIMARY KEY(\"TUID\" AUTOINCREMENT)\n" +
                        ");");

                loadData(filePath);
            }
        }
    }

    //load data into tables from file
    private static void loadData(String filePath) throws IOException, SQLException, ClassNotFoundException, ParseException {
        System.out.println("Inserting Data...");
        Connection con;
        PreparedStatement prep;
        con = getConnection();
        File file = new File(filePath);

        Scanner f = new Scanner(file);
        String courseTitle = null;
        int creditHours = 0;
        String c[];
        LinkedList classes = new LinkedList();
        while(f.hasNext()){
            c = f.nextLine().split("\t");
            switch(c[0]){
                case "CSC 105":
                    courseTitle = "Computers and Programming";
                    creditHours = 4;
                    break;
                case "CSC 107":
                    courseTitle = "Introduction to Code Preparation";
                    creditHours = 1;
                    break;
                case "CSC 116":
                    courseTitle = "Programming I";
                    creditHours = 4;
                    break;
                case "CSC 216":
                    courseTitle = "Programming II";
                    creditHours = 4;
                    break;
                case "CSC 227":
                    courseTitle = "Commenting and Naming Conventions";
                    creditHours = 2;
                    break;
                case "CSC 316":
                    courseTitle = "Data Structures & Algorithms";
                    creditHours = 4;
                    break;
                case "CSC 416":
                    courseTitle = "Advanced Algorithm Analysis";
                    creditHours = 3;
                    break;
                case "CSC 211":
                    courseTitle = "Introductory .NET Development";
                    creditHours = 3;
                    break;
                case "CSC 311":
                    courseTitle = "Advanced .NET Development";
                    creditHours = 4;
                    break;
                case "CSC 313":
                    courseTitle = "Real World Application Development";
                    creditHours = 3;
                    break;
                case "CSC 411":
                    courseTitle = "Data Driven Systems";
                    creditHours = 3;
                    break;
                case "CSC 412":
                    courseTitle = "Sensor Systems";
                    creditHours = 3;
                    break;
                case "CSC 413":
                    courseTitle = "Artificial Intelligence Systems";
                    creditHours = 3;
                    break;
                case "CSC 496":
                    courseTitle = "Software Engineering I";
                    creditHours = 4;
                    break;
                case "CSC 497":
                    courseTitle = "Software Engineering II";
                    creditHours = 4;
                    break;
                case "CSC 498":
                    courseTitle = "Software Engineering III";
                    creditHours = 4;
                    break;
            }

            prep = con.prepareStatement("INSERT INTO COURSES_TABLE VALUES(?,?,?,?);");
            prep.setString(2, c[0]);
            prep.setString(3, courseTitle);
            prep.setInt(4,creditHours);
            prep.execute();

            prep = con.prepareStatement("INSERT INTO PROFESSOR_TABLE VALUES(?,?);");
            prep.setString(2,c[1]);
            prep.execute();

            classes.add(c);
        }
        prep = con.prepareStatement("INSERT INTO CLASSROOM_TABLE VALUES(?,?,?);");
        prep.setString(2,"A");
        prep.setInt(3,30);
        prep.execute();

        prep = con.prepareStatement("INSERT INTO CLASSROOM_TABLE VALUES(?,?,?);");
        prep.setString(2,"B");
        prep.setInt(3,25);
        prep.execute();

        prep = con.prepareStatement("INSERT INTO CLASSROOM_TABLE VALUES(?,?,?);");
        prep.setString(2,"C");
        prep.setInt(3,20);
        prep.execute();

        deleteDuplicate();
        schedule(file);
    }

    //delete duplicate entries in tables
    private static  void deleteDuplicate() throws SQLException, ClassNotFoundException {
        Connection con;
        Statement state;
        con = getConnection();


        state = con.createStatement();
        state.executeUpdate("DELETE FROM COURSES_TABLE\n" +
                "WHERE EXISTS (\n" +
                "  SELECT 1 FROM COURSES_TABLE C2 \n" +
                "  WHERE COURSES_TABLE.COURSE_ID = C2.COURSE_ID\n" +
                "  AND COURSES_TABLE.COURSE_TITLE = C2.COURSE_TITLE\n" +
                "  AND COURSES_TABLE.rowid > C2.rowid\n" +
                ");");
        state.executeUpdate("DELETE FROM PROFESSOR_TABLE\n" +
                "WHERE EXISTS (\n" +
                "  SELECT 1 FROM PROFESSOR_TABLE P2 \n" +
                "  WHERE PROFESSOR_TABLE.PROFESSOR_NAME = P2.PROFESSOR_NAME\n" +
                "  AND PROFESSOR_TABLE.rowid > P2.rowid\n" +
                ");");
    }

    //schedule classes from inputted file
    private static void schedule(File file) throws SQLException, ClassNotFoundException, FileNotFoundException, ParseException {
        System.out.println("Scheduling...");

        Connection con;
        Statement state;
        PreparedStatement prep;
        ResultSet res;
        con = getConnection();
        state = con.createStatement();

        //create 2d arrays to store time blocks that are taken/open
        int[][] classroomA = new int[5][17];
        int[][] classroomB = new int[5][17];
        int[][] classroomC = new int[5][17];

        //load time blocks
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 17; j++) {
                classroomA[i][j] = 0;
                classroomB[i][j] = 0;
                classroomC[i][j] = 0;
            }
        }

        int creditHours = 0;
        int courseTUID = 0;
        int profTUID = 0;
        Scanner f = new Scanner(file);
        String c[];
        Boolean canSchedule = true;

        int numOfBlocks = 0;
        while (f.hasNext()) { //while the file has classes
            c = f.nextLine().split("\t"); //create an array to store each classes details
            Boolean scheduled = false;
            prep = con.prepareStatement("SELECT CREDIT_HOURS FROM COURSES_TABLE WHERE COURSE_ID = ?;");
            prep.setString(1, c[0]);
            res = prep.executeQuery();
            creditHours = res.getInt(1); //get credit hours

            prep = con.prepareStatement("SELECT TUID FROM COURSES_TABLE WHERE COURSE_ID = ?;");
            prep.setString(1,c[0]);
            res = prep.executeQuery();
            courseTUID = res.getInt(1); //get course tuid

            prep = con.prepareStatement("SELECT TUID FROM PROFESSOR_TABLE WHERE PROFESSOR_NAME = ?;");
            prep.setString(1,c[1]);
            res = prep.executeQuery();
            profTUID = res.getInt(1); //get professor tuid

            int section = 1;

            prep = con.prepareStatement("SELECT COUNT(TUID), COURSE_TUID FROM SCHEDULE_TABLE WHERE COURSE_TUID = ? GROUP BY COURSE_TUID;");
            prep.setInt(1,courseTUID);
            res = prep.executeQuery();
            section += res.getInt(1); //get section

            String startTime = c[3];
            String endTime = c[4];
            int startTimeIndex = startTimeIndex(startTime); //convert start time string to an index
            int endTimeIndex = endTimeIndex(endTime); //convert end time string to an index
            int room = 0;

            if (creditHours == 4 || creditHours == 3) { //schedule algorithm for 3 and 4 credit classes
                //amount of 30 min blocks each take up -1 for end time
                if (creditHours == 4) {
                    numOfBlocks = 4;
                } else {
                    numOfBlocks = 3;
                }
                scheduled = false;
                if (c[2].equals("MW")) { //schedule monday wednesday classes
                    for (int i = 0; i < numOfBlocks; i++) { //check if requested time is taken
                        if (classroomA[0][startTimeIndex + i] == 1 || classroomA[2][startTimeIndex + i] == 1) {
                            canSchedule = false;
                        }
                    }
                    if (canSchedule) { //if its free
                        for (int i = 0; i < numOfBlocks; i++) {
                            classroomA[0][startTimeIndex + i] = 1;
                            classroomA[2][startTimeIndex + i] = 1;
                        }
                        //add to schedule table in db
                        room = 1;
                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                        prep.setInt(2, courseTUID);
                        prep.setInt(3, section);
                        prep.setInt(4, room);
                        prep.setInt(5, profTUID);
                        prep.setString(6, indexToTime(startTimeIndex));
                        prep.setString(7, indexToTime(endTimeIndex));
                        prep.setString(8, c[2]);
                        prep.execute();
                        scheduled = true;
                    } else { //if its taken check next room
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[0][startTimeIndex + i] == 1 || classroomB[2][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[0][startTimeIndex + i] = 1;
                                classroomB[2][startTimeIndex + i] = 1;
                            }
                            room = 2;
                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                            prep.setInt(2, courseTUID);
                            prep.setInt(3, section);
                            prep.setInt(4, room);
                            prep.setInt(5, profTUID);
                            prep.setString(6, indexToTime(startTimeIndex));
                            prep.setString(7, indexToTime(endTimeIndex));
                            prep.setString(8, c[2]);
                            prep.execute();
                            scheduled = true;
                        } else { //if its taken check next room
                            canSchedule = true;
                            for (int i = 0; i < numOfBlocks; i++) {
                                if (classroomC[0][startTimeIndex + i] == 1 || classroomC[2][startTimeIndex + i] == 1) {
                                    canSchedule = false;
                                }
                            }
                            if (canSchedule) {
                                for (int i = 0; i < numOfBlocks; i++) {
                                    classroomC[0][startTimeIndex + i] = 1;
                                    classroomC[2][startTimeIndex + i] = 1;
                                }
                                room = 3;
                                prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                prep.setInt(2, courseTUID);
                                prep.setInt(3, section);
                                prep.setInt(4, room);
                                prep.setInt(5, profTUID);
                                prep.setString(6, indexToTime(startTimeIndex));
                                prep.setString(7, indexToTime(endTimeIndex));
                                prep.setString(8, c[2]);
                                prep.execute();
                                scheduled = true;
                            } else { //if cant find spot for requested time/day check next day (all three rooms)
                                canSchedule = true;
                                for (int i = 0; i < numOfBlocks; i++) {
                                    if (classroomA[1][startTimeIndex + i] == 1 || classroomA[3][startTimeIndex + i] == 1) {
                                        canSchedule = false;
                                    }
                                }
                                if (canSchedule) {
                                    for (int i = 0; i < numOfBlocks; i++) {
                                        classroomA[1][startTimeIndex + i] = 1;
                                        classroomA[3][startTimeIndex + i] = 1;
                                    }
                                    room = 1;
                                    prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                    prep.setInt(2, courseTUID);
                                    prep.setInt(3, section);
                                    prep.setInt(4, room);
                                    prep.setInt(5, profTUID);
                                    prep.setString(6, indexToTime(startTimeIndex));
                                    prep.setString(7, indexToTime(endTimeIndex));
                                    prep.setString(8, "TR");
                                    prep.execute();
                                    scheduled = true;
                                } else {
                                    canSchedule = true;
                                    for (int i = 0; i < numOfBlocks; i++) {
                                        if (classroomB[1][startTimeIndex + i] == 1 || classroomB[3][startTimeIndex + i] == 1) {
                                            canSchedule = false;
                                        }
                                    }
                                    if (canSchedule) {
                                        for (int i = 0; i < numOfBlocks; i++) {
                                            classroomB[1][startTimeIndex + i] = 1;
                                            classroomB[3][startTimeIndex + i] = 1;
                                        }
                                        room = 2;
                                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                        prep.setInt(2, courseTUID);
                                        prep.setInt(3, section);
                                        prep.setInt(4, room);
                                        prep.setInt(5, profTUID);
                                        prep.setString(6, indexToTime(startTimeIndex));
                                        prep.setString(7, indexToTime(endTimeIndex));
                                        prep.setString(8, "TR");
                                        prep.execute();
                                        scheduled = true;
                                    } else {
                                        canSchedule = true;
                                        for (int i = 0; i < numOfBlocks; i++) {
                                            if (classroomC[1][startTimeIndex + i] == 1 || classroomC[3][startTimeIndex + i] == 1) {
                                                canSchedule = false;
                                            }
                                        }
                                        if (canSchedule) {
                                            for (int i = 0; i < numOfBlocks; i++) {
                                                classroomC[1][startTimeIndex + i] = 1;
                                                classroomC[3][startTimeIndex + i] = 1;
                                            }
                                            room = 3;
                                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                            prep.setInt(2, courseTUID);
                                            prep.setInt(3, section);
                                            prep.setInt(4, room);
                                            prep.setInt(5, profTUID);
                                            prep.setString(6, indexToTime(startTimeIndex));
                                            prep.setString(7, indexToTime(endTimeIndex));
                                            prep.setString(8, "TR");
                                            prep.execute();
                                            scheduled = true;
                                        } else { //if failed to find on next day, loop through requested day (all three rooms)
                                            while (startTimeIndex < 12 && endTimeIndex < 16) {
                                                startTimeIndex = endTimeIndex;
                                                endTimeIndex += numOfBlocks;
                                                canSchedule = true;
                                                for (int i = 0; i < numOfBlocks; i++) {
                                                    if (classroomA[0][startTimeIndex + i] == 1 || classroomA[2][startTimeIndex + i] == 1) {
                                                        canSchedule = false;
                                                    }
                                                }
                                                if (canSchedule) {
                                                    for (int i = 0; i < numOfBlocks; i++) {
                                                        classroomA[0][startTimeIndex + i] = 1;
                                                        classroomA[2][startTimeIndex + i] = 1;
                                                    }
                                                    room = 1;
                                                    prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                                    prep.setInt(2, courseTUID);
                                                    prep.setInt(3, section);
                                                    prep.setInt(4, room);
                                                    prep.setInt(5, profTUID);
                                                    prep.setString(6, indexToTime(startTimeIndex));
                                                    prep.setString(7, indexToTime(endTimeIndex));
                                                    prep.setString(8, c[2]);
                                                    prep.execute();
                                                    scheduled = true;
                                                    break;
                                                } else {
                                                    canSchedule = true;
                                                    for (int i = 0; i < numOfBlocks; i++) {
                                                        if (classroomB[0][startTimeIndex + i] == 1 || classroomB[2][startTimeIndex + i] == 1) {
                                                            canSchedule = false;
                                                        }
                                                    }
                                                    if (canSchedule) {
                                                        for (int i = 0; i < numOfBlocks; i++) {
                                                            classroomB[0][startTimeIndex + i] = 1;
                                                            classroomB[2][startTimeIndex + i] = 1;
                                                        }
                                                        room = 2;
                                                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                                        prep.setInt(2, courseTUID);
                                                        prep.setInt(3, section);
                                                        prep.setInt(4, room);
                                                        prep.setInt(5, profTUID);
                                                        prep.setString(6, indexToTime(startTimeIndex));
                                                        prep.setString(7, indexToTime(endTimeIndex));
                                                        prep.setString(8, c[2]);
                                                        prep.execute();
                                                        scheduled = true;
                                                        break;
                                                    } else {
                                                        canSchedule = true;
                                                        for (int i = 0; i < numOfBlocks; i++) {
                                                            if (classroomC[0][startTimeIndex + i] == 1 || classroomC[2][startTimeIndex + i] == 1) {
                                                                canSchedule = false;
                                                            }
                                                        }
                                                        if (canSchedule) {
                                                            for (int i = 0; i < numOfBlocks; i++) {
                                                                classroomC[0][startTimeIndex + i] = 1;
                                                                classroomC[2][startTimeIndex + i] = 1;
                                                            }
                                                            room = 3;
                                                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                                            prep.setInt(2, courseTUID);
                                                            prep.setInt(3, section);
                                                            prep.setInt(4, room);
                                                            prep.setInt(5, profTUID);
                                                            prep.setString(6, indexToTime(startTimeIndex));
                                                            prep.setString(7, indexToTime(endTimeIndex));
                                                            prep.setString(8, c[2]);
                                                            prep.execute();
                                                            scheduled = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (c[2].equals("TR")) { //do the same as above for tuesday/thursday classes
                    canSchedule = true;
                    for (int i = 0; i < numOfBlocks; i++) {
                        if (classroomB[1][startTimeIndex + i] == 1 || classroomB[3][startTimeIndex + i] == 1) {
                            canSchedule = false;
                        }
                    }
                    if(canSchedule){
                        for (int i = 0; i < numOfBlocks; i++) {
                            classroomA[1][startTimeIndex + i] = 1;
                            classroomA[3][startTimeIndex + i] = 1;
                        }
                        room = 1;
                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                        prep.setInt(2, courseTUID);
                        prep.setInt(3, section);
                        prep.setInt(4, room);
                        prep.setInt(5, profTUID);
                        prep.setString(6, indexToTime(startTimeIndex));
                        prep.setString(7, indexToTime(endTimeIndex));
                        prep.setString(8, c[2]);
                        prep.execute();
                        scheduled = true;
                    }else {
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[1][startTimeIndex + i] == 1 || classroomB[3][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[1][startTimeIndex + i] = 1;
                                classroomB[3][startTimeIndex + i] = 1;
                            }
                            room = 2;
                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                            prep.setInt(2, courseTUID);
                            prep.setInt(3, section);
                            prep.setInt(4, room);
                            prep.setInt(5, profTUID);
                            prep.setString(6, indexToTime(startTimeIndex));
                            prep.setString(7, indexToTime(endTimeIndex));
                            prep.setString(8, c[2]);
                            prep.execute();
                            scheduled = true;
                        } else {
                            canSchedule = true;
                            for (int i = 0; i < numOfBlocks; i++) {
                                if (classroomC[1][startTimeIndex + i] == 1 || classroomC[3][startTimeIndex + i] == 1) {
                                    canSchedule = false;
                                }
                            }
                            if (canSchedule) {
                                for (int i = 0; i < numOfBlocks; i++) {
                                    classroomC[1][startTimeIndex + i] = 1;
                                    classroomC[3][startTimeIndex + i] = 1;
                                }
                                room = 3;
                                prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                prep.setInt(2, courseTUID);
                                prep.setInt(3, section);
                                prep.setInt(4, room);
                                prep.setInt(5, profTUID);
                                prep.setString(6, indexToTime(startTimeIndex));
                                prep.setString(7, indexToTime(endTimeIndex));
                                prep.setString(8, c[2]);
                                prep.execute();
                                scheduled = true;
                            }else {
                                canSchedule = true;
                                for (int i = 0; i < numOfBlocks; i++) {
                                    if (classroomA[0][startTimeIndex + i] == 1 || classroomA[2][startTimeIndex + i] == 1) {
                                        canSchedule = false;
                                    }
                                }
                                if (canSchedule) {
                                    for (int i = 0; i < numOfBlocks; i++) {
                                        classroomA[0][startTimeIndex + i] = 1;
                                        classroomA[2][startTimeIndex + i] = 1;
                                    }
                                    room = 1;
                                    prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                    prep.setInt(2, courseTUID);
                                    prep.setInt(3, section);
                                    prep.setInt(4, room);
                                    prep.setInt(5, profTUID);
                                    prep.setString(6, indexToTime(startTimeIndex));
                                    prep.setString(7, indexToTime(endTimeIndex));
                                    prep.setString(8, "MW");
                                    prep.execute();
                                    scheduled = true;
                                }else{
                                    canSchedule = true;
                                    for (int i = 0; i < numOfBlocks; i++) {
                                        if (classroomB[0][startTimeIndex + i] == 1 || classroomB[2][startTimeIndex + i] == 1) {
                                            canSchedule = false;
                                        }
                                    }
                                    if (canSchedule) {
                                        for (int i = 0; i < numOfBlocks; i++) {
                                            classroomB[0][startTimeIndex + i] = 1;
                                            classroomB[2][startTimeIndex + i] = 1;
                                        }
                                        room = 2;
                                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                        prep.setInt(2, courseTUID);
                                        prep.setInt(3, section);
                                        prep.setInt(4, room);
                                        prep.setInt(5, profTUID);
                                        prep.setString(6, indexToTime(startTimeIndex));
                                        prep.setString(7, indexToTime(endTimeIndex));
                                        prep.setString(8, "MW");
                                        prep.execute();
                                        scheduled = true;
                                    }else{
                                        canSchedule = true;
                                        for (int i = 0; i < numOfBlocks; i++) {
                                            if (classroomC[0][startTimeIndex + i] == 1 || classroomC[2][startTimeIndex + i] == 1) {
                                                canSchedule = false;
                                            }
                                        }
                                        if (canSchedule) {
                                            for (int i = 0; i < numOfBlocks; i++) {
                                                classroomC[0][startTimeIndex + i] = 1;
                                                classroomC[2][startTimeIndex + i] = 1;
                                            }
                                            room = 3;
                                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                            prep.setInt(2, courseTUID);
                                            prep.setInt(3, section);
                                            prep.setInt(4, room);
                                            prep.setInt(5, profTUID);
                                            prep.setString(6, indexToTime(startTimeIndex));
                                            prep.setString(7, indexToTime(endTimeIndex));
                                            prep.setString(8, "MW");
                                            prep.execute();
                                            scheduled = true;
                                        }else{
                                            while (startTimeIndex < 12 && endTimeIndex < 16) {
                                                startTimeIndex = endTimeIndex;
                                                endTimeIndex += numOfBlocks;
                                                canSchedule = true;
                                                for (int i = 0; i < numOfBlocks; i++) {
                                                    if (classroomA[1][startTimeIndex + i] == 1 || classroomA[3][startTimeIndex + i] == 1) {
                                                        canSchedule = false;
                                                    }
                                                }
                                                if (canSchedule) {
                                                    for (int i = 0; i < numOfBlocks; i++) {
                                                        classroomA[1][startTimeIndex + i] = 1;
                                                        classroomA[3][startTimeIndex + i] = 1;
                                                    }
                                                    room = 1;
                                                    prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                                    prep.setInt(2, courseTUID);
                                                    prep.setInt(3, section);
                                                    prep.setInt(4, room);
                                                    prep.setInt(5, profTUID);
                                                    prep.setString(6, indexToTime(startTimeIndex));
                                                    prep.setString(7, indexToTime(endTimeIndex));
                                                    prep.setString(8, c[2]);
                                                    prep.execute();
                                                    scheduled = true;
                                                } else {
                                                    canSchedule = true;
                                                    for (int i = 0; i < numOfBlocks; i++) {
                                                        if (classroomB[1][startTimeIndex + i] == 1 || classroomB[3][startTimeIndex + i] == 1) {
                                                            canSchedule = false;
                                                        }
                                                    }
                                                    if (canSchedule) {
                                                        for (int i = 0; i < numOfBlocks; i++) {
                                                            classroomB[1][startTimeIndex + i] = 1;
                                                            classroomB[3][startTimeIndex + i] = 1;
                                                        }
                                                        room = 2;
                                                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                                        prep.setInt(2, courseTUID);
                                                        prep.setInt(3, section);
                                                        prep.setInt(4, room);
                                                        prep.setInt(5, profTUID);
                                                        prep.setString(6, indexToTime(startTimeIndex));
                                                        prep.setString(7, indexToTime(endTimeIndex));
                                                        prep.setString(8, c[2]);
                                                        prep.execute();
                                                        scheduled = true;
                                                    } else {
                                                        canSchedule = true;
                                                        for (int i = 0; i < numOfBlocks; i++) {
                                                            if (classroomC[1][startTimeIndex + i] == 1 || classroomC[3][startTimeIndex + i] == 1) {
                                                                canSchedule = false;
                                                            }
                                                        }
                                                        if (canSchedule) {
                                                            for (int i = 0; i < numOfBlocks; i++) {
                                                                classroomC[1][startTimeIndex + i] = 1;
                                                                classroomC[3][startTimeIndex + i] = 1;
                                                            }
                                                            room = 3;
                                                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                                            prep.setInt(2, courseTUID);
                                                            prep.setInt(3, section);
                                                            prep.setInt(4, room);
                                                            prep.setInt(5, profTUID);
                                                            prep.setString(6, indexToTime(startTimeIndex));
                                                            prep.setString(7, indexToTime(endTimeIndex));
                                                            prep.setString(8, c[2]);
                                                            prep.execute();
                                                            scheduled = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else if (creditHours == 2 || creditHours == 1) { //schedule 1 and 2 credit classes
                if(creditHours == 2) {
                    numOfBlocks = 4;
                }else{
                    numOfBlocks = 2;
                }
                if(c[2].equals("M")){ //schedule monday class, check all three rooms
                    canSchedule = true;
                    for (int i = 0; i < numOfBlocks; i++) {
                        if (classroomA[0][startTimeIndex + i] == 1) {
                            canSchedule = false;
                        }
                    }
                    if (canSchedule) {
                        for (int i = 0; i < numOfBlocks; i++) {
                            classroomA[0][startTimeIndex + i] = 1;
                        }
                        room = 1;
                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                        prep.setInt(2, courseTUID);
                        prep.setInt(3, section);
                        prep.setInt(4, room);
                        prep.setInt(5, profTUID);
                        prep.setString(6, indexToTime(startTimeIndex));
                        prep.setString(7, indexToTime(endTimeIndex));
                        prep.setString(8, c[2]);
                        prep.execute();
                        scheduled = true;
                    }else{
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[0][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[0][startTimeIndex + i] = 1;
                            }
                            room = 2;
                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                            prep.setInt(2, courseTUID);
                            prep.setInt(3, section);
                            prep.setInt(4, room);
                            prep.setInt(5, profTUID);
                            prep.setString(6, indexToTime(startTimeIndex));
                            prep.setString(7, indexToTime(endTimeIndex));
                            prep.setString(8, c[2]);
                            prep.execute();
                            scheduled = true;
                        }else{
                            canSchedule = true;
                            for (int i = 0; i < numOfBlocks; i++) {
                                if (classroomC[0][startTimeIndex + i] == 1) {
                                    canSchedule = false;
                                }
                            }
                            if (canSchedule) {
                                for (int i = 0; i < numOfBlocks; i++) {
                                    classroomC[0][startTimeIndex + i] = 1;
                                }
                                room = 3;
                                prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                prep.setInt(2, courseTUID);
                                prep.setInt(3, section);
                                prep.setInt(4, room);
                                prep.setInt(5, profTUID);
                                prep.setString(6, indexToTime(startTimeIndex));
                                prep.setString(7, indexToTime(endTimeIndex));
                                prep.setString(8, c[2]);
                                prep.execute();
                                scheduled = true;
                            }
                        }
                    }
                }else if(c[2].equals("T")){ //schedule tuesday classes, check all three rooms
                    canSchedule = true;
                    for (int i = 0; i < numOfBlocks; i++) {
                        if (classroomA[1][startTimeIndex + i] == 1) {
                            canSchedule = false;
                        }
                    }
                    if (canSchedule) {
                        for (int i = 0; i < numOfBlocks; i++) {
                            classroomA[1][startTimeIndex + i] = 1;
                        }
                        room = 1;
                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                        prep.setInt(2, courseTUID);
                        prep.setInt(3, section);
                        prep.setInt(4, room);
                        prep.setInt(5, profTUID);
                        prep.setString(6, indexToTime(startTimeIndex));
                        prep.setString(7, indexToTime(endTimeIndex));
                        prep.setString(8, c[2]);
                        prep.execute();
                        scheduled = true;
                    }else{
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[1][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[1][startTimeIndex + i] = 1;
                            }
                            room = 2;
                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                            prep.setInt(2, courseTUID);
                            prep.setInt(3, section);
                            prep.setInt(4, room);
                            prep.setInt(5, profTUID);
                            prep.setString(6, indexToTime(startTimeIndex));
                            prep.setString(7, indexToTime(endTimeIndex));
                            prep.setString(8, c[2]);
                            prep.execute();
                            scheduled = true;
                        }else{
                            canSchedule = true;
                            for (int i = 0; i < numOfBlocks; i++) {
                                if (classroomC[1][startTimeIndex + i] == 1) {
                                    canSchedule = false;
                                }
                            }
                            if (canSchedule) {
                                for (int i = 0; i < numOfBlocks; i++) {
                                    classroomC[1][startTimeIndex + i] = 1;
                                }
                                room = 3;
                                prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                prep.setInt(2, courseTUID);
                                prep.setInt(3, section);
                                prep.setInt(4, room);
                                prep.setInt(5, profTUID);
                                prep.setString(6, indexToTime(startTimeIndex));
                                prep.setString(7, indexToTime(endTimeIndex));
                                prep.setString(8, c[2]);
                                prep.execute();
                                scheduled = true;
                            }
                        }
                    }
                }else if(c[2].equals("W")){ //schedule wednesday classes, check all three rooms
                    canSchedule = true;
                    for (int i = 0; i < numOfBlocks; i++) {
                        if (classroomA[2][startTimeIndex + i] == 1) {
                            canSchedule = false;
                        }
                    }
                    if (canSchedule) {
                        for (int i = 0; i < numOfBlocks; i++) {
                            classroomA[2][startTimeIndex + i] = 1;
                        }
                        room = 1;
                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                        prep.setInt(2, courseTUID);
                        prep.setInt(3, section);
                        prep.setInt(4, room);
                        prep.setInt(5, profTUID);
                        prep.setString(6, indexToTime(startTimeIndex));
                        prep.setString(7, indexToTime(endTimeIndex));
                        prep.setString(8, c[2]);
                        prep.execute();
                        scheduled = true;
                    }else{
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[2][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[2][startTimeIndex + i] = 1;
                            }
                            room = 2;
                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                            prep.setInt(2, courseTUID);
                            prep.setInt(3, section);
                            prep.setInt(4, room);
                            prep.setInt(5, profTUID);
                            prep.setString(6, indexToTime(startTimeIndex));
                            prep.setString(7, indexToTime(endTimeIndex));
                            prep.setString(8, c[2]);
                            prep.execute();
                            scheduled = true;
                        }else{
                            canSchedule = true;
                            for (int i = 0; i < numOfBlocks; i++) {
                                if (classroomC[2][startTimeIndex + i] == 1) {
                                    canSchedule = false;
                                }
                            }
                            if (canSchedule) {
                                for (int i = 0; i < numOfBlocks; i++) {
                                    classroomC[2][startTimeIndex + i] = 1;
                                }
                                room = 3;
                                prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                prep.setInt(2, courseTUID);
                                prep.setInt(3, section);
                                prep.setInt(4, room);
                                prep.setInt(5, profTUID);
                                prep.setString(6, indexToTime(startTimeIndex));
                                prep.setString(7, indexToTime(endTimeIndex));
                                prep.setString(8, c[2]);
                                prep.execute();
                                scheduled = true;
                            }
                        }
                    }
                }else if(c[2].equals("R")){ //schedule thurdays classes, check all three rooms
                    canSchedule = true;
                    for (int i = 0; i < numOfBlocks; i++) {
                        if (classroomA[3][startTimeIndex + i] == 1) {
                            canSchedule = false;
                        }
                    }
                    if (canSchedule) {
                        for (int i = 0; i < numOfBlocks; i++) {
                            classroomA[3][startTimeIndex + i] = 1;
                        }
                        room = 1;
                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                        prep.setInt(2, courseTUID);
                        prep.setInt(3, section);
                        prep.setInt(4, room);
                        prep.setInt(5, profTUID);
                        prep.setString(6, indexToTime(startTimeIndex));
                        prep.setString(7, indexToTime(endTimeIndex));
                        prep.setString(8, c[2]);
                        prep.execute();
                        scheduled = true;
                    }else{
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[3][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[3][startTimeIndex + i] = 1;
                            }
                            room = 2;
                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                            prep.setInt(2, courseTUID);
                            prep.setInt(3, section);
                            prep.setInt(4, room);
                            prep.setInt(5, profTUID);
                            prep.setString(6, indexToTime(startTimeIndex));
                            prep.setString(7, indexToTime(endTimeIndex));
                            prep.setString(8, c[2]);
                            prep.execute();
                            scheduled = true;
                        }else{
                            canSchedule = true;
                            for (int i = 0; i < numOfBlocks; i++) {
                                if (classroomC[3][startTimeIndex + i] == 1) {
                                    canSchedule = false;
                                }
                            }
                            if (canSchedule) {
                                for (int i = 0; i < numOfBlocks; i++) {
                                    classroomC[3][startTimeIndex + i] = 1;
                                }
                                room = 3;
                                prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                prep.setInt(2, courseTUID);
                                prep.setInt(3, section);
                                prep.setInt(4, room);
                                prep.setInt(5, profTUID);
                                prep.setString(6, indexToTime(startTimeIndex));
                                prep.setString(7, indexToTime(endTimeIndex));
                                prep.setString(8, c[2]);
                                prep.execute();
                                scheduled = true;
                            }
                        }
                    }
                }
                if(!scheduled){ //if a 1 or 2 credit class cannot be scheudled, loop through the days to find a time that it can be scheduled
                    int dayIndex = dayToIndex(c[2]);
                    while(dayIndex <= 4){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomA[dayIndex][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomA[dayIndex][startTimeIndex + i] = 1;
                            }
                            room = 1;
                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                            prep.setInt(2, courseTUID);
                            prep.setInt(3, section);
                            prep.setInt(4, room);
                            prep.setInt(5, profTUID);
                            prep.setString(6, indexToTime(startTimeIndex));
                            prep.setString(7, indexToTime(endTimeIndex));
                            prep.setString(8, indexToDay(dayIndex));
                            prep.execute();
                            scheduled = true;
                        }else{
                            canSchedule = true;
                            for (int i = 0; i < numOfBlocks; i++) {
                                if (classroomB[dayIndex][startTimeIndex + i] == 1) {
                                    canSchedule = false;
                                }
                            }
                            if (canSchedule) {
                                for (int i = 0; i < numOfBlocks; i++) {
                                    classroomB[dayIndex][startTimeIndex + i] = 1;
                                }
                                room = 2;
                                prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                prep.setInt(2, courseTUID);
                                prep.setInt(3, section);
                                prep.setInt(4, room);
                                prep.setInt(5, profTUID);
                                prep.setString(6, indexToTime(startTimeIndex));
                                prep.setString(7, indexToTime(endTimeIndex));
                                prep.setString(8, indexToDay(dayIndex));
                                prep.execute();
                                scheduled = true;
                            }else{
                                canSchedule = true;
                                for (int i = 0; i < numOfBlocks; i++) {
                                    if (classroomC[dayIndex][startTimeIndex + i] == 1) {
                                        canSchedule = false;
                                    }
                                }
                                if (canSchedule) {
                                    for (int i = 0; i < numOfBlocks; i++) {
                                        classroomC[dayIndex][startTimeIndex + i] = 1;
                                    }
                                    room = 3;
                                    prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                                    prep.setInt(2, courseTUID);
                                    prep.setInt(3, section);
                                    prep.setInt(4, room);
                                    prep.setInt(5, profTUID);
                                    prep.setString(6, indexToTime(startTimeIndex));
                                    prep.setString(7, indexToTime(endTimeIndex));
                                    prep.setString(8, indexToDay(dayIndex));
                                    prep.execute();
                                    scheduled = true;
                                }
                            }
                        }
                        dayIndex++;
                    }
                }
            }
            if(!scheduled){ //if all else fails check friday, all three classes
                //adjust number of 30 min blocks each course takes up
                if(creditHours == 4){
                    numOfBlocks = 8;
                    startTimeIndex = 0;
                }else if(creditHours == 3){
                    numOfBlocks = 6;
                    startTimeIndex = 0;
                }else{
                    startTimeIndex = 0;
                }
                canSchedule = true;
                for (int i = 0; i < numOfBlocks; i++) {
                    if (classroomA[4][startTimeIndex + i] == 1) {
                        canSchedule = false;
                    }
                }
                if (canSchedule) {
                    for (int i = 0; i < numOfBlocks; i++) {
                        classroomA[4][startTimeIndex + i] = 1;
                        endTimeIndex = startTimeIndex+i;
                    }
                    room = 1;
                    prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                    prep.setInt(2, courseTUID);
                    prep.setInt(3, section);
                    prep.setInt(4, room);
                    prep.setInt(5, profTUID);
                    prep.setString(6, indexToTime(startTimeIndex));
                    prep.setString(7, indexToTime(endTimeIndex));
                    prep.setString(8, "F");
                    prep.execute();
                    scheduled = true;
                }else{
                    canSchedule = true;
                    for (int i = 0; i < numOfBlocks; i++) {
                        if (classroomB[4][startTimeIndex + i] == 1) {
                            canSchedule = false;
                        }
                    }
                    if (canSchedule) {
                        for (int i = 0; i < numOfBlocks; i++) {
                            classroomB[4][startTimeIndex + i] = 1;
                            endTimeIndex = startTimeIndex+i;
                        }
                        room = 2;
                        prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                        prep.setInt(2, courseTUID);
                        prep.setInt(3, section);
                        prep.setInt(4, room);
                        prep.setInt(5, profTUID);
                        prep.setString(6, indexToTime(startTimeIndex));
                        prep.setString(7, indexToTime(endTimeIndex));
                        prep.setString(8, "F");
                        prep.execute();
                        scheduled = true;
                    }else{
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomC[4][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomC[4][startTimeIndex + i] = 1;
                                endTimeIndex = startTimeIndex+i;
                            }
                            room = 3;
                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                            prep.setInt(2, courseTUID);
                            prep.setInt(3, section);
                            prep.setInt(4, room);
                            prep.setInt(5, profTUID);
                            prep.setString(6, indexToTime(startTimeIndex));
                            prep.setString(7, indexToTime(endTimeIndex));
                            prep.setString(8, "F");
                            prep.execute();
                            scheduled = true;
                        }else{ //if no time slot can be found on any day, label as unscheduled
                            prep = con.prepareStatement("INSERT INTO SCHEDULE_TABLE VALUES(?,?,?,?,?,?,?,?)");
                            prep.setInt(2, courseTUID);
                            prep.setInt(3, section);
                            prep.setString(4, "N/A");
                            prep.setInt(5, profTUID);
                            prep.setString(6, "N/A");
                            prep.setString(7, "N/A");
                            prep.setString(8, "N/A");
                            prep.execute();
                        }
                    }
                }
            }
        }
    }

    //if db already exists, pre-populate 2d arrays with already exisiting schedule
    public static void returningUser(boolean DBExists) throws ClassNotFoundException, SQLException, IOException, ParseException{
        Connection con;
        ResultSet res,res2;
        PreparedStatement prep;
        con = getConnection();

        int[][] classroomA = new int[5][17];
        int[][] classroomB = new int[5][17];
        int[][] classroomC = new int[5][17];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 17; j++) {
                classroomA[i][j] = 0;
                classroomB[i][j] = 0;
                classroomC[i][j] = 0;
            }
        }
        int numOfBlocks = 0;
        Boolean canSchedule;

        if(DBExists){
            //get all info from DB excluding courses that are unschduled
            prep = con.prepareStatement("SELECT START_TIME, END_TIME, CLASSROOM_TUID, DAYS, COURSE_TUID FROM SCHEDULE_TABLE WHERE DAYS <> ?;");
            prep.setString(1,"N/A");
            res = prep.executeQuery();
            while(res.next()){
                //assign info from course to variables
                String startTime = res.getString(1);
                String endTime = res.getString(2);
                int room = res.getInt(3);
                String days = res.getString(4);
                int courseTUID = res.getInt(5);
                int startTimeIndex = startTimeIndex(startTime);
                int endTimeIndex = endTimeIndex(endTime);
                prep = con.prepareStatement("SELECT CREDIT_HOURS FROM COURSES_TABLE WHERE TUID = ?;");
                prep.setInt(1,courseTUID);
                res2 = prep.executeQuery(); //get credit hours from course tuid
                int creditHours = res2.getInt(1);

                if(creditHours == 4 || creditHours == 3){ //add 3 and 4 credit classes to 2d array
                    if(creditHours == 4){
                        numOfBlocks = 4;
                    }else{
                        numOfBlocks = 3;
                    }
                    if(days.equals("MW") && room == 1){ //add MW classes depending on room assigned
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomA[0][startTimeIndex + i] == 1 || classroomA[2][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomA[0][startTimeIndex + i] = 1;
                                classroomA[2][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("MW") && room == 2){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[0][startTimeIndex + i] == 1 || classroomB[2][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[0][startTimeIndex + i] = 1;
                                classroomB[2][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("MW") && room == 3){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomC[0][startTimeIndex + i] == 1 || classroomC[2][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomC[0][startTimeIndex + i] = 1;
                                classroomC[2][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("TR") && room == 1){ //add TR classes depending on room assigned
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomA[1][startTimeIndex + i] == 1 || classroomA[3][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomA[1][startTimeIndex + i] = 1;
                                classroomA[3][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("TR") && room == 2){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[1][startTimeIndex + i] == 1 || classroomB[3][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[1][startTimeIndex + i] = 1;
                                classroomB[3][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("TR") && room == 3){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomC[1][startTimeIndex + i] == 1 || classroomC[3][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomC[1][startTimeIndex + i] = 1;
                                classroomC[3][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("F") && room == 1){ //add F classes depending on room assigned
                        if(creditHours == 4){
                            numOfBlocks = 8;
                        }else if(creditHours == 3){
                            numOfBlocks = 6;
                        }
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomA[4][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomA[4][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("F") && room == 2){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[4][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[4][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("F") && room == 3){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomC[4][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomC[4][startTimeIndex + i] = 1;
                            }
                        }
                    }
                }else if(creditHours == 2 || creditHours == 1){ //add 1 and 2 credit classes based on room assigned
                    if(creditHours == 2){
                        numOfBlocks = 4;
                    }else{
                        numOfBlocks = 2;
                    }
                    if(days.equals("M") && room == 1){ //add M classes depending on room assigned
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomA[0][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomA[0][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("M") && room == 2){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[0][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[0][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("M") && room == 3){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomC[0][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomC[0][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("T") && room == 1){ //add T classes depending on room assigned
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomA[1][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomA[1][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("T") && room == 2){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[1][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[1][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("T") && room == 3){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomC[1][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomC[1][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("W") && room == 1){//add W classes depending on room assigned
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomA[2][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomA[2][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("W") && room == 2){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[2][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[2][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("W") && room == 3){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomC[2][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomC[2][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("R") && room == 1){ //add R classes depending on room assigned
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomA[3][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomA[3][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("R") && room == 2){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[3][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[3][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("R") && room == 3){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomC[3][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomC[3][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("F") && room == 1){ //add F classes depending on room assigned
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomA[4][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomA[4][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("F") && room == 2){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomB[4][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomB[4][startTimeIndex + i] = 1;
                            }
                        }
                    }else if(days.equals("F") && room == 3){
                        canSchedule = true;
                        for (int i = 0; i < numOfBlocks; i++) {
                            if (classroomC[4][startTimeIndex + i] == 1) {
                                canSchedule = false;
                            }
                        }
                        if (canSchedule) {
                            for (int i = 0; i < numOfBlocks; i++) {
                                classroomC[4][startTimeIndex + i] = 1;
                            }
                        }
                    }
                }
            }
        }
    }

    public static int startTimeIndex(String startTime) { //method to convert start time string to index
        int index = 0;
        if(startTime.equals("8:30")){
            index = 0;
        }else if(startTime.equals("9:00")){
            index = 1;
        }else if(startTime.equals("9:30")){
            index = 2;
        }else if(startTime.equals("10:00")){
            index = 3;
        }else if(startTime.equals("10:30")){
            index = 4;
        }else if(startTime.equals("11:00")){
            index = 5;
        }else if(startTime.equals("11:30")){
            index = 6;
        }else if(startTime.equals("12:00")){
            index = 7;
        }else if(startTime.equals("12:30")){
            index = 8;
        }else if(startTime.equals("1:00")){
            index = 9;
        }else if(startTime.equals("1:30")){
            index = 10;
        }else if(startTime.equals("2:00")){
            index = 11;
        }else if(startTime.equals("2:30")){
            index = 12;
        }else if(startTime.equals("3:00")){
            index = 13;
        }else if(startTime.equals("3:30")){
            index = 14;
        }else if(startTime.equals("4:00")){
            index = 15;
        }else if(startTime.equals("4:30")){
            index = 16;
        }
        return index;
    }

    public static int endTimeIndex(String endTime) { //method to convert end time string to index
        int index = 0;
        if(endTime.equals("8:30")){
            index = 0;
        }else if(endTime.equals("9:00")){
            index = 1;
        }else if(endTime.equals("9:30")){
            index = 2;
        }else if(endTime.equals("10:00")){
            index = 3;
        }else if(endTime.equals("10:30")){
            index = 4;
        }else if(endTime.equals("11:00")){
            index = 5;
        }else if(endTime.equals("11:30")){
            index = 6;
        }else if(endTime.equals("12:00")){
            index = 7;
        }else if(endTime.equals("12:30")){
            index = 8;
        }else if(endTime.equals("1:00")){
            index = 9;
        }else if(endTime.equals("1:30")){
            index = 10;
        }else if(endTime.equals("2:00")){
            index = 11;
        }else if(endTime.equals("2:30")){
            index = 12;
        }else if(endTime.equals("3:00")){
            index = 13;
        }else if(endTime.equals("3:30")){
            index = 14;
        }else if(endTime.equals("4:00")){
            index = 15;
        }else if(endTime.equals("4:30")){
            index = 16;
        }
        return index;
    }

    public static String indexToTime(int index){ //method to convert time index to time string
        String time = "";
        if(index == 0){
            time = "8:30";
        }else if(index == 1){
            time = "9:00";
        }else if(index == 2){
            time = "9:30";
        }else if(index == 3){
            time = "10:00";
        }else if(index == 4){
            time = "10:30";
        }else if(index == 5){
            time = "11:00";
        }else if(index == 6){
            time = "11:30";
        }else if(index == 7){
            time = "12:00";
        }else if(index == 8){
            time = "12:30";
        }else if(index == 9){
            time = "1:00";
        }else if(index == 10){
            time = "1:30";
        }else if(index == 11){
            time = "2:00";
        }else if(index == 12){
            time = "2:30";
        }else if(index == 13){
            time = "3:00";
        }else if(index == 14){
            time = "3:30";
        }else if(index == 15){
            time = "4:00";
        }else if(index == 16){
            time = "4:30";
        }
        return time;
    }

    public static String indexToDay(int index){ //method to convert day index to string
        String day = "M";
        if(index == 0){
            day="M";
        }else if(index == 1){
            day="T";
        }else if(index == 2){
            day="W";
        }else if(index == 3){
            day="R";
        }
        return day;
    }

    public static int dayToIndex(String day){ //method to convert day string to index
        int index = 0;
        if(day.equals("M")){
            index=0;
        }else if(day.equals("T")){
            index=1;
        }else if(day.equals("W")){
            index=2;
        }else if(day.equals("R")){
            index=3;
        }
        return index;
    }

public static void displayScheduleByDay() throws SQLException, ClassNotFoundException { //display schedule sorted by days
    PreparedStatement prep;
    ResultSet res;
    Connection con;
    con= getConnection();

    prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE DAYS = ?;");
    prep.setString(1,"M");
    res = prep.executeQuery();

    System.out.println("Monday: ");
    System.out.println("| Course TUID | Professor TUID | Classroom TUID | Start Time | End Time |");
    System.out.println("--------------------------------------------------------------------------");
    while(res.next()){
        System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getInt(3)+ "\t\t\t\t"+ res.getString(4)+ "\t\t\t\t"+ res.getString(5));
    }

    prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE DAYS = ?;");
    prep.setString(1,"MW");
    res = prep.executeQuery();
    System.out.println(" ");
    System.out.println("Monday/Wednesday: ");
    System.out.println("| Course TUID | Professor TUID | Classroom TUID | Start Time | End Time |");
    System.out.println("--------------------------------------------------------------------------");
    while(res.next()){
        System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getInt(3)+ "\t\t\t\t"+ res.getString(4)+ "\t\t\t\t"+ res.getString(5));
    }

    prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE DAYS = ?;");
    prep.setString(1,"T");
    res = prep.executeQuery();
    System.out.println(" ");
    System.out.println("Tuesday: ");
    System.out.println("| Course TUID | Professor TUID | Classroom TUID | Start Time | End Time |");
    System.out.println("--------------------------------------------------------------------------");
    while(res.next()){
        System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getInt(3)+ "\t\t\t\t"+ res.getString(4)+ "\t\t\t\t"+ res.getString(5));
    }

    prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE DAYS = ?;");
    prep.setString(1,"TR");
    res = prep.executeQuery();
    System.out.println(" ");
    System.out.println("Tuesday/Thursday: ");
    System.out.println("| Course TUID | Professor TUID | Classroom TUID | Start Time | End Time |");
    System.out.println("--------------------------------------------------------------------------");
    while(res.next()){
        System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getInt(3)+ "\t\t\t\t"+ res.getString(4)+ "\t\t\t\t"+ res.getString(5));
    }

    prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE DAYS = ?;");
    prep.setString(1,"W");
    res = prep.executeQuery();
    System.out.println(" ");
    System.out.println("Wednesday: ");
    System.out.println("| Course TUID | Professor TUID | Classroom TUID | Start Time | End Time |");
    System.out.println("--------------------------------------------------------------------------");
    while(res.next()){
        System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getInt(3)+ "\t\t\t\t"+ res.getString(4)+ "\t\t\t\t"+ res.getString(5));
    }

    prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE DAYS = ?;");
    prep.setString(1,"R");
    res = prep.executeQuery();
    System.out.println(" ");
    System.out.println("Thursday: ");
    System.out.println("| Course TUID | Professor TUID | Classroom TUID | Start Time | End Time |");
    System.out.println("--------------------------------------------------------------------------");
    while(res.next()){
        System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getInt(3)+ "\t\t\t\t"+ res.getString(4)+ "\t\t\t\t"+ res.getString(5));
    }

    prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE DAYS = ?;");
    prep.setString(1,"F");
    res = prep.executeQuery();
    System.out.println(" ");
    System.out.println("Friday: ");
    System.out.println("| Course TUID | Professor TUID | Classroom TUID | Start Time | End Time |");
    System.out.println("--------------------------------------------------------------------------");
    while(res.next()){
        System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getInt(3)+ "\t\t\t\t"+ res.getString(4)+ "\t\t\t\t"+ res.getString(5));
    }

    prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID FROM SCHEDULE_TABLE WHERE DAYS = ?;");
    prep.setString(1,"N/A");
    res = prep.executeQuery();
    System.out.println(" ");
    System.out.println("Unscheduled: ");
    while(res.next()){
        System.out.println(res.getInt(1)+ " " + res.getInt(2));
    }
}

    public static void displayScheduleByProf() throws SQLException, ClassNotFoundException { //method to display schedule sorted by professor
        PreparedStatement prep;
        ResultSet res;
        Connection con;
        con= getConnection();

        prep = con.prepareStatement("SELECT COURSE_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE PROFESSOR_TUID = ?;");
        prep.setInt(1,1);
        res = prep.executeQuery();
        System.out.println(" ");
        System.out.println("James: ");
        System.out.println("| Course TUID | Classroom TUID | Start Time | End Time |");
        System.out.println("-----------------------------------------------------------");
        while(res.next()){
            System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getString(3)+ "\t\t\t\t"+ res.getString(4));
        }

        prep = con.prepareStatement("SELECT COURSE_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE PROFESSOR_TUID = ?;");
        prep.setInt(1,2);
        res = prep.executeQuery();
        System.out.println(" ");
        System.out.println("Smith: ");
        System.out.println("| Course TUID | Classroom TUID | Start Time | End Time |");
        System.out.println("-----------------------------------------------------------");
        while(res.next()){
            System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getString(3)+ "\t\t\t\t"+ res.getString(4));
        }

        prep = con.prepareStatement("SELECT COURSE_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE PROFESSOR_TUID = ?;");
        prep.setInt(1,3);
        res = prep.executeQuery();
        System.out.println(" ");
        System.out.println("Jones: ");
        System.out.println("| Course TUID | Classroom TUID | Start Time | End Time |");
        System.out.println("-----------------------------------------------------------");
        while(res.next()){
            System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getString(3)+ "\t\t\t\t"+ res.getString(4));
        }

        prep = con.prepareStatement("SELECT COURSE_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE PROFESSOR_TUID = ?;");
        prep.setInt(1,4);
        res = prep.executeQuery();
        System.out.println(" ");
        System.out.println("Vasquez: ");
        System.out.println("| Course TUID | Classroom TUID | Start Time | End Time |");
        System.out.println("-----------------------------------------------------------");
        while(res.next()){
            System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getString(3)+ "\t\t\t\t"+ res.getString(4));
        }

        prep = con.prepareStatement("SELECT COURSE_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE PROFESSOR_TUID = ?;");
        prep.setInt(1,5);
        res = prep.executeQuery();
        System.out.println(" ");
        System.out.println("Abdul: ");
        System.out.println("| Course TUID | Classroom TUID | Start Time | End Time |");
        System.out.println("-----------------------------------------------------------");
        while(res.next()){
            System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getString(3)+ "\t\t\t\t"+ res.getString(4));
        }

        prep = con.prepareStatement("SELECT COURSE_TUID, CLASSROOM_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE PROFESSOR_TUID = ?;");
        prep.setInt(1,6);
        res = prep.executeQuery();
        System.out.println(" ");
        System.out.println("Thomas: ");
        System.out.println("| Course TUID | Classroom TUID | Start Time | End Time |");
        System.out.println("-----------------------------------------------------------");
        while(res.next()){
            System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getString(3)+ "\t\t\t\t"+ res.getString(4));
        }
    }

    public static void displayScheduleByClassroom() throws SQLException, ClassNotFoundException { //method to display schedule sorted by classroom
        PreparedStatement prep;
        ResultSet res;
        Connection con;
        con= getConnection();

        prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE CLASSROOM_TUID = ?;");
        prep.setInt(1,1);
        res = prep.executeQuery();
        System.out.println(" ");
        System.out.println("A: ");
        System.out.println("| Course TUID | Professor TUID | Start Time | End Time |");
        System.out.println("-----------------------------------------------------------");
        while(res.next()){
            System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getString(3)+ "\t\t\t\t"+ res.getString(4));
        }

        prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE CLASSROOM_TUID = ?;");
        prep.setInt(1,3);
        res = prep.executeQuery();
        System.out.println(" ");
        System.out.println("B: ");
        System.out.println("| Course TUID | Professor TUID | Start Time | End Time |");
        System.out.println("-----------------------------------------------------------");
        while(res.next()){
            System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getString(3)+ "\t\t\t\t"+ res.getString(4));
        }

        prep = con.prepareStatement("SELECT COURSE_TUID, PROFESSOR_TUID, START_TIME, END_TIME FROM SCHEDULE_TABLE WHERE CLASSROOM_TUID = ?;");
        prep.setInt(1,3);
        res = prep.executeQuery();
        System.out.println(" ");
        System.out.println("C: ");
        System.out.println("| Course TUID | Professor TUID | Start Time | End Time |");
        System.out.println("-----------------------------------------------------------");
        while(res.next()){
            System.out.println("\t"+res.getInt(1)+ "\t\t\t\t" + res.getInt(2)+ "\t\t\t\t"+ res.getString(3)+ "\t\t\t\t"+ res.getString(4));
        }
    }


    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException, ParseException {
        ResultSet rs;
        Connection con = null;
        boolean DBExists = false;
        Scanner s = new Scanner(System.in);
        System.out.print("Enter file path to load classes: ");
        String filePath = s.next(); //get file path from input
        Boolean exists = new File("ScheduleMaker.db").isFile(); //check if the DB exists
        if(exists){ //if it does exist
            int selection = 0;
            while(selection != 5){ //show menu items
                System.out.println("Please Select an Option 1-5: ");
                System.out.println("\t 1. Display Schedule Sorted By Days");
                System.out.println("\t 2. Display Schedule Sorted By Classrooms");
                System.out.println("\t 3. Display Schedule Sorted By Professors");
                System.out.println("\t 4. Add More Classes");
                System.out.println("\t 5. Quit");
                System.out.print("Enter Selection: ");
                selection = s.nextInt();

                if(selection == 1){ //run function based on input
                    displayScheduleByDay();
                }else if(selection == 2){
                    displayScheduleByClassroom();
                }else if(selection == 3){
                    displayScheduleByProf();
                }else if(selection == 4){
                    returningUser(DBExists);
                    System.out.println("Enter file path to load classes: ");
                    String fp = s.next();
                    File newFile = new File(fp);
                    schedule(newFile);
                }else if(selection==5){
                    System.out.println("Would you like to delete DB? [y,n]: ");
                    if(s.next().equals("y")){
                        File db = new File("ScheduleMaker.db");
                        db.delete();
                    }
                    break;
                }else{
                    System.out.println("Not a Valid Selection");
                }
            }
        }else { //if db does not exist
            try {
                getConnection(); //connect
                buildDatabase(DBExists, filePath); //build and schedule

            } catch (Exception e) {
                e.printStackTrace();
            }
            int selection = 0;
            while (selection != 4) { //once finished display menu
                System.out.println("Please Select an Option 1-4: ");
                System.out.println("\t 1. Display Schedule Sorted By Days");
                System.out.println("\t 2. Display Schedule Sorted By Classrooms");
                System.out.println("\t 3. Display Schedule Sorted By Professors");
                System.out.println("\t 4. Quit");
                System.out.print("Enter Selection: ");
                selection = s.nextInt();

                if (selection == 1) {
                    displayScheduleByDay();
                } else if (selection == 2) {
                    displayScheduleByClassroom();
                } else if (selection == 3) {
                    displayScheduleByProf();
                } else if (selection == 4) {
                    System.out.println("Would you like to delete DB? [y,n]: ");
                    if (s.next().equals("y")) {
                        File db = new File("ScheduleMaker.db");
                        db.delete();
                        break;
                    } else {
                        System.out.println("Not a Valid Selection");
                    }
                }
            }
        }
    }
}
