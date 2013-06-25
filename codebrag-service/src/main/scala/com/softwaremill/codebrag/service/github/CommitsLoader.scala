package com.softwaremill.codebrag.service.github

import com.softwaremill.codebrag.domain.CommitInfo

trait CommitsLoader {
  def loadMissingCommits(repoData: RepoData): List[CommitInfo]
}
