ALTER TABLE task
ADD COLUMN user_id UUID,
ADD CONSTRAINT fk_user
FOREIGN KEY (user_id) REFERENCES epicuser(id);