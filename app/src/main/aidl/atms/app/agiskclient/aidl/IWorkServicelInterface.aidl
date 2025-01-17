// IWorkServicelInterface.aidl
package atms.app.agiskclient.aidl;

import atms.app.agiskclient.aidl.IWorkListener;
// Declare any non-default types here with import statements

interface IWorkServicelInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
            int getPid();
                int getUid();
                String getUUID();
                IBinder getFileSystemService();
                boolean doWork(String xml,IWorkListener listener);
                int getTaskNum();
                void terminateSelf();
                String getTasksState();
                String getConclusionMsg();

                //Direct function (Extended Function)
                String direct_GetPartListString(String driver);
                boolean direct_DeletePart(String driver,int number);
                boolean direct_NewPart(String driver, long start_byte, long end_byte, String code,String name);
                boolean direct_File_ForceWrite(String content,String dest);
                String direct_File_ForceRead(String path);
}
