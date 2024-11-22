package forms

import forms.behaviours.BooleanFieldBehaviours
import play.api.data.FormError

class HaveYouBeenSelfEmployedInLast17YearsFormProviderSpec extends BooleanFieldBehaviours {

  val requiredKey = "haveYouBeenSelfEmployedInLast17Years.error.required"
  val invalidKey = "error.boolean"

  val form = new HaveYouBeenSelfEmployedInLast17YearsFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, invalidKey)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }
}
