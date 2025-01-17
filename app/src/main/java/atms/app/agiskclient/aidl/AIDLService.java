/*
 * Copyright 2021 John "topjohnwu" Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package atms.app.agiskclient.aidl;


import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.ipc.RootService;
import com.topjohnwu.superuser.nio.FileSystemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import atms.app.agiskclient.ConfigBox.ActionBase;
import atms.app.agiskclient.ConfigBox.OrigConfig;

// Demonstrate RootService using AIDL (daemon mode)
class AIDLService extends RootService {
    static String TAG = "AIDLService:";
    static final String TASKNAME_KEY = "taskname";
    static final String LOG_KEY = "log";
    static final String PERCENTAGE_KEY = "percentage";
    static final String TASK_PID_KEY = "pid";
    static final String TASK_TID_KEY = "tid";

    static {
        // Only load the library when this class is loaded in a root process.
        // The classloader will load this class (and call this static block) in the non-root
        // process because we accessed it when constructing the Intent to send.
        // Add this check so we don't unnecessarily
        // load native code that'll never be used.
        //
    }

    static {
        System.loadLibrary("agisk-cli");
    }

    // Demonstrate we can also run native code via JNI with RootServices
    native int nativeGetUid();

    native void writelog(String log);
    native boolean forceWriteToFileWithRoot(String content, String dest);
    native String forceReadWithRoot(String path);

    native void writelogTag(String tag, String log);

    native String getPartListString(String driver);

    native boolean deletePart(String driver, int number);

    native boolean newPart(String driver, long start_byte, long end_byte, String code, String name);

    List<String> logList = new ArrayList<>();

    StringBuilder logstring = new StringBuilder();

    public void upgradeProgress(int percentage, String log) {
        logList.add("[" + percentage + "] " + log + "\n");
    }

    public void onTasksFailed(String failed_name, String log) {
        logList.add("[Error] [" + failed_name + "] " + log + "\n");
    }

    public void onTaskStarted(int pid, int tid) {
        logList.add("[Start] [pid] " + pid + " [tid] " + tid + "\n");
    }


    //////////////////////////////////////////////

    class MyIPC extends IWorkServicelInterface.Stub {
        AtomicInteger tasknum = new AtomicInteger(-1);

        public int getTaskNumber() {
            return tasknum.get();
        }

        /**
         * this method exist for noreason
         * but i still ofer it
         *
         * @return
         */
        public int incrementTaskNum() {
            return tasknum.incrementAndGet();
        }

        private void setTaskNumber(int total_size) {
            tasknum.set(total_size);
        }

        private int decrementTaskNumber() {
            return tasknum.decrementAndGet();
        }


        /**
         * @return
         */
        @Override
        public int getTaskNum() {
            return getTaskNumber();
        }

        @Override
        public String getTasksState() throws RemoteException {

            return logList.get(logList.size() - 1);
        }

        @Override
        public String getConclusionMsg() throws RemoteException {
            for (String s : logList) {
                logstring.append(s);
            }
            return logstring.toString();
        }

        @Override
        public void terminateSelf() throws RemoteException {
            stopSelf();
        }

        @Override
        public int getPid() {
            return Process.myPid();
        }

        @Override
        public int getUid() {
            return Process.myUid();
        }

        @Override
        public String getUUID() {
            return uuid;
        }

        @Override
        public IBinder getFileSystemService() {
            return FileSystemManager.getService();
        }

        @Override
        public String direct_GetPartListString(String driver) throws RemoteException {

            return getPartListString(driver);
        }

        @Override
        public boolean direct_File_ForceWrite(String content, String dest) throws RemoteException {
            return forceWriteToFileWithRoot(content, dest);
        }
        @Override
        public String direct_File_ForceRead(String path) throws RemoteException {
            return forceReadWithRoot(path);
        }

        @Override
        public boolean direct_DeletePart(String driver, int number) throws RemoteException {
            return !deletePart(driver,number);
        }

        @Override
        public boolean direct_NewPart(String driver, long start_byte, long end_byte, String code,String name) throws RemoteException {
            return !newPart(driver,start_byte,end_byte,code,name);
        }



        @Override
        public boolean doWork(String xml,IWorkListener iWorkListener) throws RemoteException {
            writelog("Dowork start work");

            try {
                writelog("really start to work");
                //start work
                OrigConfig origConfig = new OrigConfig(xml, "UTF-8");

                origConfig.setActionList();
                List<ActionBase> actionBaseList = origConfig.getActionList();
                writelogTag(atms.app.agiskclient.Tools.TAG.WorkerTAG, "List size:" + actionBaseList.size());
                //
                int finished=0;
                int total_size = actionBaseList.size();
                setTaskNumber(total_size);
                writelogTag(TAG, "Worker.taskNum " + getTaskNumber());
                boolean ret=false;
                for (ActionBase actionBase : actionBaseList) {

                    writelogTag(atms.app.agiskclient.Tools.TAG.WorkerTAG, "Put a task : " + actionBase.getTaskID());
                    final ActionBase.ActionType actionType = actionBase.actiontype;
                    writelogTag(TAG, "Type " + actionType);
                    //TODO get return
                    switch (actionType) {
                        case ACTION_TYPE_PARTITION:
                            writelogTag(TAG, "Partition type");
                            ret=actionBase.doAction();
                            writelogTag(TAG, "Partition done");
                            break;
                        case ACTION_TYPE_DISK:
                            writelogTag(TAG, "Disk type");
                            ret=actionBase.doAction();
                            writelogTag(TAG, "Disk done");
                            break;
                        default:
                            writelogTag(TAG, "Default type");
                            ret=actionBase.doAction();
                            writelogTag(TAG, "Default done");
                            break;
                    }
                    int least = decrementTaskNumber();
                    writelogTag(TAG, "Left task [" + least + "]");
                    updateProgress(least, total_size, "Task [" + actionBase.getTaskID() + "] finished");
                    finished++;
                    if (!ret) {
                        iWorkListener.onThrowWarning("Sub task failed : "+finished);
                        iWorkListener.onCompleted(false);
                        return false;
                    }

                    iWorkListener.onProgress(finished,total_size);
                }

                writelogTag(TAG, "All task finished.exit");
                updateProgress(0, total_size, "All task finished.exit");

                iWorkListener.onCompleted(true);
                return true;
            } catch (Exception exception) {
                writelogTag(TAG, exception.getMessage());
            }
            return false;
        }


        public void updateProgress(int left, int total, String log) {
            writelogTag(TAG, "Left " + left);
            writelogTag(TAG, "Total " + total);
            double per = ((left * 100 / total));
            writelogTag(TAG, "Progress " + per);
            upgradeProgress((int) per, log);
        }

    }

    ////////////////////////////////////////////////////////////
    private final String uuid = UUID.randomUUID().toString();

    @Override
    public void onCreate() {
        Log.d(TAG, "AIDLService: onCreate, " + uuid);
    }

    @Override
    public void onRebind(@NonNull Intent intent) {
        // This callback will be called when we are reusing a previously started root process
        Log.d(TAG, "AIDLService: onRebind, daemon process reused");
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        Log.d(TAG, "AIDLService: onBind");
        return new MyIPC();
    }

    @Override
    public boolean onUnbind(@NonNull Intent intent) {
        Log.d(TAG, "AIDLService: onUnbind, client process unbound");
        // Return true here so onRebind will be called
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "AIDLService: onDestroy");
    }
}
