package com.softwaremill.codebrag.service.github.jgit

import com.softwaremill.codebrag.service.github.{CommitImportService, GitHubCommitImportServiceFactory}
import com.softwaremill.codebrag.dao.{UserDAO, CommitInfoDAO}
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import com.softwaremill.codebrag.common.EventBus
import com.softwaremill.codebrag.service.config.CodebragConfig
import com.typesafe.scalalogging.slf4j.Logging

class JgitGitHubCommitImportServiceFactory(commitInfoDao: CommitInfoDAO,
                                           userDao: UserDAO,
                                           eventBus: EventBus,
                                           codebragConfiguration: CodebragConfig) extends GitHubCommitImportServiceFactory with Logging {

  def createInstance(login: String): CommitImportService = {
    val importingUserOpt = userDao.findByLoginOrEmail(login)

      val token = importingUserOpt match {
        case Some(user) => user.authentication.token
        case None => {
          logger.warn(s"User $login not found in DB. Cannot properly initialize commit importer")
          s"user-$login-not-found"
        }
      }
    val credentials = new UsernamePasswordCredentialsProvider(token, "")
    new CommitImportService(
      new JgitCommitsLoader(
        new JgitFacade(credentials),
        new InternalGitDirTree(codebragConfiguration),
        new JgitLogConverter,
        commitInfoDao),
      commitInfoDao,
      eventBus)
  }

}
