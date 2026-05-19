import type { App } from 'vue'
import {
  Label,
  SelectRoot,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectGroup,
  SelectLabel,
  SelectItem,
  CheckboxRoot,
  CheckboxIndicator,
} from 'radix-vue'

export default {
  install: (app: App) => {
    app.component('RLabel', Label)
    app.component('RSelect', SelectRoot)
    app.component('RSelectTrigger', SelectTrigger)
    app.component('RSelectValue', SelectValue)
    app.component('RSelectContent', SelectContent)
    app.component('RSelectGroup', SelectGroup)
    app.component('RSelectLabel', SelectLabel)
    app.component('RSelectItem', SelectItem)
    app.component('RCheckbox', CheckboxRoot)
    app.component('RCheckboxIndicator', CheckboxIndicator)
  },
}
