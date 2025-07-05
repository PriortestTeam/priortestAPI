package com.hu.oneclick.manager;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.util.Set;

public class GitOperation {
    private final String username;
    private final String password;
    private final String gitUrl;
    private final String remoteName;
    private String localRepoDirPath;

    public GitOperation(String username, String password, String gitUrl, String remoteName, String localRepoDirPath) {
        this.username = username;
        this.password = password;
        this.gitUrl = gitUrl + ".git";
        this.remoteName = String.format("%s%s", remoteName, System.currentTimeMillis());
        this.localRepoDirPath = localRepoDirPath + "/.git";
    }

    public void push() throws Exception {
        File localRepo = new File(localRepoDirPath);
        FileRepositoryBuilder repoBuilder = new FileRepositoryBuilder();
        Repository repository = null;
        Git git = null;
        try {
            repository = repoBuilder.setGitDir(localRepo).readEnvironment().findGitDir().build();
            if (!localRepo.exists()) {
                repository.create();
            }

            git = new Git(repository);
            git.remoteSetUrl().setRemoteUri(new URIish(gitUrl)).setRemoteName(this.remoteName).call();
            git.add().addFilepattern(".").call();
            git.commit().setMessage(String.valueOf(System.currentTimeMillis())).call();
            git.push().setRemote(this.remoteName).setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
            git.remoteRemove().setRemoteName(this.remoteName).call();
        } finally {
            if (git != null) {
                Set<String> names = git.getRepository().getRemoteNames();
                if (names.contains(this.remoteName)) {
                    git.remoteRemove().setRemoteName(this.remoteName).call();
                }
                git.close();
            }
            if (repository != null) {
                repository.close();
            }
        }
    }
}
