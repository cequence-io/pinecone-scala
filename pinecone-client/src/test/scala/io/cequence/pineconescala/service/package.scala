package io.cequence.pineconescala

import org.scalatest.{Assertion, AsyncTestSuite}
import org.scalatest.concurrent.Eventually.{eventually, PatienceConfig}

import scala.concurrent.{ExecutionContext, Future}

package object service {

  trait EventuallyAssert { this: AsyncTestSuite =>

    def eventuallyAssert[A](f: () => Future[A])(check: A => Boolean)
                           (implicit config: PatienceConfig): Future[Assertion] =
      eventually {
        f().map(a => assert(check(a)))
      }

  }

}
