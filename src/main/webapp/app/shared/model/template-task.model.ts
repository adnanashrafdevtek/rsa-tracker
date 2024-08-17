import dayjs from 'dayjs';
import { ITemplate } from 'app/shared/model/template.model';
import { ITemplateChecklist } from 'app/shared/model/template-checklist.model';

export interface ITemplateTask {
  id?: number;
  name?: string;
  description?: string | null;
  createdDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  modifiedDate?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  template?: ITemplate | null;
  checklists?: ITemplateChecklist[] | null;
}

export const defaultValue: Readonly<ITemplateTask> = {};
