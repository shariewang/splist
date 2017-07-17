package palie.splist.ocr;

import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import palie.splist.ListActivity;

public class AsyncProcessTask extends AsyncTask<String, String, Boolean> {

    public AsyncProcessTask(ListActivity activity) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
    }

    private ProgressDialog dialog;
    /** application context. */
    private final ListActivity activity;

    protected void onPreExecute() {
        dialog.setMessage("Processing");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    protected void onPostExecute(Boolean result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        activity.updateResults(result);
    }

    @Override
    protected Boolean doInBackground(String... args) {

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            Client restClient = new Client();
            restClient.applicationId = "Splist -- group shopping lists";
            // You should get e-mail from ABBYY Cloud OCR SDK service with the application password
            restClient.password = "xk9/xinj+q6UpZwv65koWgbt";

            publishProgress( "Uploading image...");

            String language = "English"; // Comma-separated list: Japanese,English or German,French,Spanish etc.

            ReceiptSettings receiptSettings = new ReceiptSettings();

//
//            ProcessingSettings processingSettings = new ProcessingSettings();
//            processingSettings.setOutputFormat( ProcessingSettings.OutputFormat.txt );
//            processingSettings.setLanguage(language);

            publishProgress("Uploading..");

            Task task = restClient.processReceipt(inputFile, receiptSettings);

            while( task.isTaskActive() ) {
                // Note: it's recommended that your application waits
                // at least 2 seconds before making the first getTaskStatus request
                // and also between such requests for the same task.
                // Making requests more often will not improve your application performance.
                // Note: if your application queues several files and waits for them
                // it's recommended that you use listFinishedTasks instead (which is described
                // at http://ocrsdk.com/documentation/apireference/listFinishedTasks/).

                Thread.sleep(5000);
                publishProgress( "Waiting.." );
                task = restClient.getTaskStatus(task.Id);
            }

            if( task.Status == Task.TaskStatus.Completed ) {
                publishProgress( "Downloading.." );
                FileOutputStream fos = activity.openFileOutput(outputFile,Context.MODE_PRIVATE);

                try {
                    restClient.downloadResult(task, fos);
                } finally {
                    fos.close();
                }

                publishProgress( "Ready" );
            } else if( task.Status == Task.TaskStatus.NotEnoughCredits ) {
                throw new Exception( "Not enough credits to process task. Add more pages to your application's account." );
            } else {
                throw new Exception( "Task failed" );
            }

            return true;
        } catch (Exception e) {
            final String message = "Error: " + e.getMessage();
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        // TODO Auto-generated method stub
        String stage = values[0];
        dialog.setMessage(stage);
    }

}
