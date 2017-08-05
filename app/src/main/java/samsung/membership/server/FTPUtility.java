package samsung.membership.server;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

    public class FTPUtility {
        private String server = "";
        private int port = 0;
        private String id = "";
        private String password = "";
        FTPClient ftpClient;

        public FTPUtility(String server, int port, String id, String password) throws IOException {
            this.server = server;
            this.port = port;
            this.id = id;
            this.password = password;
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("euc-kr");
        }

        public boolean login() {
            try {
                if (ftpClient.isConnected() == true) {
                    return ftpClient.login(id, password);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        public void connect() {
            try {
                ftpClient.connect(server, port);
                int reply = 0;

                reply = ftpClient.getReplyCode();
                if (FTPReply.isPositiveCompletion(reply) == false) {
                    ftpClient.disconnect();
                    Log.d("TAG","서버 연결 실패");
                    Log.d("TAG","REPLY");

                } else {
                    Log.d("TAG", "서버 연결 성공");
                }
                ftpClient.enterLocalPassiveMode();

            } catch (Exception e) {
                if (ftpClient.isConnected() == true) {
                    try {
                        ftpClient.disconnect();
                    } catch (IOException f) {

                    }
                }
                e.printStackTrace();
                Log.d("TAG", "ERROR");
            }
        }

        public boolean logout() {
            try {
                return ftpClient.logout();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public FTPFile[] list() {
            FTPFile[] files = null;
            try {
                files = ftpClient.listFiles();
                return files;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        //파일전송을 받는다
        public File get(String source, String target) {

            /*OutputStream output = null;
            File f;

            try {
                FTPFile ftpfile[] = ftpClient.listFiles();
                for(int i=0;i<ftpfile.length;i++) {
                    Log.d("NAME" , ftpfile[i].getName());
                    f = new File(source,ftpfile[i].getName());
                    output = new FileOutputStream(f);
                    ftpClient.retrieveFile(ftpfile[i].getName(),output);
                    output.close();
                }
                //File local = new File(source);
                //output = new FileOutputStream(local);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            /*File file = new File(source);
            try {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE); //이 코드는 반드시 서버와 커넥션이후에 써야된다
                boolean flag = ftpClient.retrieveFile(target, output);
                output.flush();
                output.close();

                if (flag == true) {
                    return file;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            return null;
        }

        //파일 업로드
        public boolean upload(File file) {
            boolean resultCode = false;
            try {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE); //이 코드는 반드시 서버와 커넥션이후에 써야된다
                FileInputStream fis = new FileInputStream(file);
                boolean isSuccess = ftpClient.storeFile(file.getName(), fis);
                if (isSuccess == true) {
                    resultCode = true;
                }
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultCode;
        }
        public void cd(String path) {
            try {
                ftpClient.changeWorkingDirectory(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void pwd() {
            try {
                Log.d("PWD",ftpClient.printWorkingDirectory());
            } catch (Exception e) {

            }
        }
        public void disconnect() {
            try {
                ftpClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }