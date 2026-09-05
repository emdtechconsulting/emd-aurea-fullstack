package com.emdtech.aurea.migration.firebase.dryrun;

public class FirebaseBackupDryRunMain {

    public static void main(String[] args)
            throws Exception {

        if (args.length == 0) {

            System.out.println(
                    "Uso:"
            );

            System.out.println(
                    "FirebaseBackupDryRunMain "
                            + "<ruta-del-backup.json>"
            );

            return;
        }

        FirebaseBackupDryRun dryRun =
                new FirebaseBackupDryRun();

        dryRun.execute(
                args[0]
        );
    }
}