#include <connectionHandler.h>

//hey nir

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_){}
ConnectionHandler::ConnectionHandler():host_(),port_(), io_service_(), socket_(io_service_){};

ConnectionHandler::~ConnectionHandler() {
    close();
}


short ConnectionHandler::bytesToShort(char* bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}


ConnectionHandler::shortToBytes(short num, char* bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);

}


void ConnectionHandler::decode(String& msg) {
    string decodedMsg = "";
    char opCode[2] = {msg[0],msg[1]};
    msg = msg.substr(2);
    switch (bytesToShort(opCode)) {
        case 9:  //if message is notification
            //add to decoded message noitifcation identifier
            decodedMsg += "NOTIFICATION ";
            //check if notification is pm or public and add PM or PUBLIC accordingly
            char pmPublic = msg[0];
            switch (pmPublic) {
                case '0':
                    decodedMsg += "PM ";
                    break;
                case '1':
                    decodedMsg += "PUBLIC ";
                    break;
            }
            msg = msg.substr(1);
            decodedMsg += "from ";
            // the rest of the message until the byte symbol \0 is the userName 
            while (msg[0] != '\0') {
                decodedMsg += msg[0];
                msg = msg.substr(1);
            }
            //instead of 0 add a dot and space (NOTIFICATION PM/PUBLIS from USERNAME: )
            decodedMsg += ": "
            // the rest of the message until the byte symbol \0 which if followed by ';' is the content
            while (msg[0] != '\0') {
                decodedMsg += msg[0];
                msg = msg.substr(1);
            }
            //now message is complete so change message to be decodedMsg
            msg = decodedMsg;
            break;
        case 10: //if message is ACK

            break;
        case 11: //if message is error

            break;

    }
}

void ConnectionHandler::encode(String& msg) {

}




    // Connect to the remote machine
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to "
        << host_ << ":" << port_ << std::endl;

    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}


    // Read a fixed number of bytes from the server - blocking.
    // Returns false in case the connection is closed before bytesToRead bytes can be read.
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        cout << "try before while" << endl;
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
        cout << "try after while" << endl;
        if (error) {
            cout << "if after while. meaning theres error " << boost::system::system_error(error).what()<< endl;
            throw boost::system::system_error(error);
        }
    } catch (std::exception& e) {
        cout << "getBytes"<< endl;
       // std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

	// Send a fixed number of bytes from the client - blocking.
    // Returns false in case the connection is closed before all the data is sent.
bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            cout << "writing " << bytes << endl;
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        cout << "sendBytes encountered error: " <<e.what()<< endl;
    //    std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}



// String msgToRead = ""
// while ( getLine (msgToRead) != true ){}

    // Read an ascii line from the server
    // Returns false in case connection closed before a newline can be read.
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\n');
}


// String opCode = "9"
// String opCode = "09"


// String msgToRead = ""
// while ( getFrameAscii( msgToRead.getBytes(UTF8) ,';') != true ){}

	// Send an ascii line from the server
    // Returns false in case connection closed before all the data is sent.
bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\n');
}


    // Get Ascii data from the server until the delimiter character
    // Returns false in case connection closed before null can be read.
bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
		do{
            cout << "THIS IS A CALL TO GET FRAME" << endl;
			getBytes(&ch, 1);
            frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        cout<<"getFrameAscii"<<endl;
     //   std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

    // sendFramAscii ( MSG , ';' )
    // Send a message to the remote host.
    // Returns false in case connection is closed before all the data is sent.
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
    if (!result) {
        cout << "couldnt sendBytes " << frame.c_str() << endl;
        return false;

    }
	return sendBytes(&delimiter,1);
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}