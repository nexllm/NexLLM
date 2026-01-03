import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'NexLLM',
          title: 'NexLLM',
          href: 'https://github.com/nexllm/NexLLM',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/nexllm/NexLLM',
          blankTarget: true,
        }
      ]}
    />
  );
};

export default Footer;
