/*
 * Copyright 2024 HM Revenue & Customs
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

package navigation

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import controllers.routes
import pages._
import models._

@Singleton
class Navigator @Inject() {

  private val normalRoutes: Page => UserAnswers => Call = {
    case HaveYouSpokenToSomeoneAboutSelfEmploymentOrLivingAbroadPage => haveYouSpoken
    case TimeSpentOutsideUkPage                                      => timeSpentOutsideUk
    case HaveYouBeenSelfEmployedInLast17YearsPage                    => selfEmployed
    case LivedOrWorkedOutsideUkPage                                  => livedOrWorkedOutsideUk
    case NationalInsuranceContributionsPage                          => nationalInsuranceContributions
    case _ =>                                                      _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case HaveYouSpokenToSomeoneAboutSelfEmploymentOrLivingAbroadPage => _ => routes.CheckYourAnswersController.onPageLoad()
    case TimeSpentOutsideUkPage                                      => _ => routes.CheckYourAnswersController.onPageLoad()
    case HaveYouBeenSelfEmployedInLast17YearsPage                    => _ => routes.CheckYourAnswersController.onPageLoad()
    case LivedOrWorkedOutsideUkPage                                  => _ => routes.CheckYourAnswersController.onPageLoad()
    case NationalInsuranceContributionsPage                          => _ => routes.CheckYourAnswersController.onPageLoad()
    case _ => _ => routes.CheckYourAnswersController.onPageLoad()
  }

  private def nationalInsuranceContributions(answers: UserAnswers): Call =
    answers.get(NationalInsuranceContributionsPage).map {
      case NationalInsuranceContributions.Beforestatepensionage =>
        routes.JourneyRecoveryController.onPageLoad()
      case NationalInsuranceContributions.Reachstatepensionage =>
        routes.JourneyRecoveryController.onPageLoad()
      case NationalInsuranceContributions.AlreadyStoppedPaying =>
        routes.JourneyRecoveryController.onPageLoad()
      case NationalInsuranceContributions.StoppedGettingCredits =>
        routes.JourneyRecoveryController.onPageLoad()
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def livedOrWorkedOutsideUk(answers: UserAnswers): Call =
    answers.get(LivedOrWorkedOutsideUkPage).map {
      case true => routes.TimeSpentOutsideUkController.onPageLoad(NormalMode)
      case false => routes.NationalInsuranceContributionsController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def haveYouSpoken(answers: UserAnswers): Call =
    answers.get(HaveYouSpokenToSomeoneAboutSelfEmploymentOrLivingAbroadPage).map {
      case true => routes.LivedOrWorkedOutsideUkController.onPageLoad(NormalMode)
      case false => routes.HaveYouBeenSelfEmployedInLast17YearsController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def selfEmployed(answers: UserAnswers): Call =
    answers.get(HaveYouBeenSelfEmployedInLast17YearsPage).map {
      case true => routes.PeriodsOfSelfEmploymentController.onPageLoad(NormalMode)
      case false => routes.LivedOrWorkedOutsideUkController.onPageLoad(NormalMode)
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())

  private def timeSpentOutsideUk(answers: UserAnswers): Call =
    answers.get(TimeSpentOutsideUkPage).map {
      case TimeSpentOutsideUk.Yes => routes.PeriodsOfSelfEmploymentController.onPageLoad(NormalMode)
      case TimeSpentOutsideUk.No => routes.NationalInsuranceContributionsController.onPageLoad(NormalMode)
      case TimeSpentOutsideUk.NotKnown => routes.JourneyRecoveryController.onPageLoad()
    }.getOrElse(routes.JourneyRecoveryController.onPageLoad())


  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
